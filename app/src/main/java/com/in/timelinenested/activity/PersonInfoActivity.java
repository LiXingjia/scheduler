package com.in.timelinenested.activity;


import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Link_user2;
import com.in.timelinenested.R;
import com.in.timelinenested.bean.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;


/**
 * Created by Administrator on 2018/6/4.
 */

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_back;
    private TextView tv_alias,tv_birth;
    private Button btn_addFriend;
    private String alias,picHead,birth;
    private ImageView pic;

    //Date birth;
    User userReturn;
    private com.in.timelinenested.viewClass.LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinfo);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));

        Intent intent = getIntent();
        Bundle bundleM = intent.getBundleExtra("data");

        userReturn = (User) bundleM.getSerializable("user");

        alias = userReturn.getNickname();

        //BmobDate转化成时间戳
        long t = BmobDate.getTimeStamp(userReturn.getU_birth().getDate());
//                    Log.v("key",getDateToString(t,"yyyy-MM-dd"));
        birth = getDateToString(t,"yyyy-MM-dd");

        picHead = userReturn.getU_pic_url().getFileUrl();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 1000);

    }

    private void init(){
        context = this;

        tv_alias = findViewById(R.id.tv_alias);
        tv_birth = findViewById(R.id.tv_birth);
        pic = findViewById(R.id.civ_image);

        tv_alias.setText(alias);
        tv_birth.setText(birth);

        new Thread(new Runnable(){

            @Override
            public void run() {
                final Drawable drawable = loadImageFromNetwork(picHead);
                // post() 特别关键，就是到UI主线程去更新图片
                pic.post(new Runnable(){
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        pic.setImageDrawable(drawable) ;
                    }}) ;
            }

        }).start()  ;


        tv_back = findViewById(R.id.tv_back);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.back);
        drawable.setBounds(0,0, com.in.timelinenested.utility.ImageUtil.dp2px(context,20), com.in.timelinenested.utility.ImageUtil.dp2px(context,20));
        tv_back.setCompoundDrawables(drawable,null,null,null);
        tv_back.setOnClickListener(this);

        btn_addFriend = findViewById(R.id.btn_addFriend);
        btn_addFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_addFriend:
                setUU_id(userReturn.getObjectId());

                dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                dialog.setSuccessful("好友添加成功！");
                dialog.show();
                handler.sendEmptyMessageDelayed(CLOSE, 2000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, com.in.timelinenested.activity.FragmentActivity.class);
                        startActivity(intent);
                    }
                }, 2000);

                break;
        }
    }

    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            InputStream is = (InputStream) new URL(imageUrl).getContent();
            drawable = Drawable.createFromStream(
                    is,null);
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable ;
    }

    //时间戳转化为给定格式
    public  String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
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

             //       Toast.makeText(PersonInfoActivity.this,"添加真实用户成功！",Toast.LENGTH_LONG).show();

                }else{
                    Log.i("error","在Link_user2表中添加一条记录失败："+e.getMessage());
                }
            }
        });
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

}
