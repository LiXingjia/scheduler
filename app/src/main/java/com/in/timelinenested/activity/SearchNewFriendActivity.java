package com.in.timelinenested.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Link_user2;
import com.in.timelinenested.MainActivity;
import com.in.timelinenested.R;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/6/4.
 */

public class SearchNewFriendActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private static final int TIME_OUT = -1;
    private static final int CLOSE = 0;
    private Context context;
    private TextView tv_searchPeople;
    private EditText et_search;
    private Button btn_close;
    private com.in.timelinenested.viewClass.LoadingDialog dialog;
    private User userReturn = new User();

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchnewfriend);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        init();
    }
    private void init(){
        context = this;

        tv_searchPeople = findViewById(R.id.tv_searchPeople);
//        tv_searchHerd = findViewById(R.id.tv_searchHerd);


        et_search = findViewById(R.id.et_search);
        btn_close = findViewById(R.id.btn_close);

        et_search.addTextChangedListener(this);
        btn_close.setOnClickListener(this);
        tv_searchPeople.setOnClickListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String content = et_search.getText().toString();
        if(content.equals("")){
            tv_searchPeople.setVisibility(View.GONE);
//            tv_searchHerd.setVisibility(View.GONE);
        }else {
            tv_searchPeople.setVisibility(View.VISIBLE);
//            tv_searchHerd.setVisibility(View.VISIBLE);
            tv_searchPeople.setText("找朋友：" + content);
//            tv_searchHerd.setText("找群组：" + content);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.tv_searchPeople:
                //搜索用户
                String phone = et_search.getText().toString().trim();
                //             Log.i("phone", "onClick: "+phone);
                if (phone.length() != 11 || !isNumeric(phone)) {
                    //                   Log.i("phone", "jinru if "+phone);
//                    Toast.makeText(SearchNewFriendActivity.this,"wrong number",Toast.LENGTH_SHORT);
                    dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                    dialog.setFailed("这不是一个手机号");
                    dialog.show();
                    handler.sendEmptyMessageDelayed(CLOSE, 2000);
                    break;
                } else {
                    addUser(phone);
//
                    break;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(1);
        finish();
    }

    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private String getCurrentYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return sdf.format(date);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_OUT:
                    dialog.setRunTimeOut("超时了，检查一下网络哦");
                    handler.sendEmptyMessageDelayed(CLOSE,2000);
                    break;
                case  CLOSE:
                    dialog.close();
                    break;
            }        }
    };

    private void addUser(String phone){
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("username",phone);
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list.size()!=0){
                        userReturn = list.get(0);//TODO 传给前端标记好友
                        findU_id(list.get(0).getObjectId());

                    }else{
                        dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                        dialog.setFailed("不存在该用户");
                        dialog.show();
                        handler.sendEmptyMessageDelayed(CLOSE, 2000);
                    }
                }else{
                    Log.i("error","查询User表失败："+e.getMessage());
                }
            }
        });
    }
    //*直接关联用户
    //TODO 2、存在则在Link_user2表中查找是否是已添加的好友
    private void findU_id(final String u_id) {
        Link_user2 link_user2 = new Link_user2();
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Link_user2> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_user_id", user.getObjectId());
        BmobQuery<Link_user2> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.addWhereEqualTo("L_linked_user_id", u_id);
        List<BmobQuery<Link_user2>> andQuery = new ArrayList<BmobQuery<Link_user2>>();
        andQuery.add(bmobQuery);
        andQuery.add(bmobQuery1);

        BmobQuery<Link_user2> query = new BmobQuery<Link_user2>();
        query.and(andQuery);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0){
                        Log.i("L_UU","已经添加过该用户");
                        dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                        dialog.setFailed("已经添加过该用户");
                        dialog.show();
                        handler.sendEmptyMessageDelayed(CLOSE, 2000);
                    }else{
                        intent = new Intent(context, com.in.timelinenested.activity.PersonInfoActivity.class);
//                    intent.putExtra("userName", userName);
//                    intent.putExtra("birth", birthday);
//                    intent.putExtra("picHead", picHead);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user",userReturn);

                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                }else{
                    Log.i("error", "查找Link_user2b表失败");
                }
            }
        });

    }
    //TODO 3、未添加的好友可以进行添加
    private void setUU_id(String u_id){
        Link_user2 link_user2 = new Link_user2();
        User user = BmobUser.getCurrentUser(User.class);
        link_user2.setL_user_id(user.getObjectId());
        link_user2.setL_linked_user_id(u_id);
        link_user2.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){

                    Toast.makeText(SearchNewFriendActivity.this,"添加真实用户成功！",Toast.LENGTH_LONG).show();


//                    String userName = userReturn.getNickname();
//                String motto = user.substring(user.indexOf("motto=") + 6, user.indexOf(", sex"));
//                String sex = user.substring(user.indexOf("sex=") + 4, user.indexOf(", birthday"));
//                String address = user.substring(user.indexOf("address=") + 8, user.indexOf(", photo"));
//                    String birthday = userReturn.getU_birth().toString();
//                    String picHead = userReturn.getU_pic_url().getFileUrl();

                }else{
                    Log.i("error","在Link_user2表中添加一条记录失败："+e.getMessage());
                }
            }
        });
    }
}
