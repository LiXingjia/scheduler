package com.in.timelinenested;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.activity.FragmentActivity;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.mock.Contact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;
import static com.in.timelinenested.SetUpActivity.getPath;

/**
 * Created by Administrator on 2018/5/28.
 */

public class ModifyInfoActivity extends Activity implements View.OnClickListener, TextWatcher {
    private Context context;
    private TextView tv_back;
    private LinearLayout myHead,myName,myBirth,connect;
    private TextView tv_myBirth,tv_complete;
    private EditText tv_myName;
    private int year,month,day;
    private DatePickerDialog birthDialog;

    private String userId,userName,birthday;

    private ImageView head_image;
    private String mpath;

    User user;
    User_virtual user_virtual;
    String user_id;
    int flag;
    private User friend = new User();

    private TextView tv_searchPeople;
    private EditText et_search;
    private com.in.timelinenested.viewClass.LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.head_info);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        init();
    }
    private void init(){
        context = this;

        tv_back = findViewById(R.id.tv_back);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.back);
        tv_back.setCompoundDrawables(drawable,null,null,null);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_complete = findViewById(R.id.tv_complete);

        myHead = findViewById(R.id.myHead);
        myName = findViewById(R.id.myName);
        connect=findViewById(R.id.myConnect);
        myBirth = findViewById(R.id.myBirth);
        tv_myName = findViewById(R.id.tv_myName);;
        tv_myBirth = findViewById(R.id.tv_myBirth);
        head_image=findViewById(R.id.head_image);
        tv_searchPeople = findViewById(R.id.tv_searchPeople);
        et_search = findViewById(R.id.et_search);

        final String IMAGE_URL;
        //获取好友信息
        Intent intent=getIntent();
        Bundle bundleM = intent.getBundleExtra("data");
        //TODO 需要判断是虚拟用户还是真实用户的ID
        flag=(Integer) bundleM.getSerializable("flag");
        //真实用户
        if(flag==1){
            user=(User) bundleM.getSerializable("user");
            IMAGE_URL=user.getU_pic_url().getFileUrl();
            userName=user.getNickname();
            long t = BmobDate.getTimeStamp(user.getU_birth().getDate());
            birthday=getDateToString(t,"yyyy-MM-dd");
            connect.setVisibility(View.INVISIBLE);//关联键不可见
        }
        else {//虚拟用户
            user_virtual=(User_virtual)bundleM.getSerializable("user");
            IMAGE_URL=user_virtual.getU_pic_url().getFileUrl();
            userName=user_virtual.getU_virtual_name();
            user_id=user_virtual.getObjectId();
            long t = BmobDate.getTimeStamp(user_virtual.getU_birth().getDate());
            birthday=getDateToString(t,"yyyy-MM-dd");
        }
        new Thread(new Runnable(){

            @Override
            public void run() {
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                // post() 特别关键，就是到UI主线程去更新图片
                head_image.post(new Runnable(){
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        head_image.setImageDrawable(drawable) ;
                    }}) ;
            }

        }).start()  ;



        tv_myName.setText(userName);
        tv_myBirth.setText(birthday);

    //    initData();
        getDate(birthday);
        birthDialog = new DatePickerDialog(context,DatePickerDialog.THEME_HOLO_LIGHT,datePickerListener,year,month,day);

        if(flag==0){
            myHead.setOnClickListener(this);
            myName.setOnClickListener(this);
            myBirth.setOnClickListener(this);
            tv_complete.setOnClickListener(this);
            et_search.addTextChangedListener(this);
            tv_searchPeople.setOnClickListener(this);

        }
        if(flag==1){
            tv_myName.setFocusable(false);
            tv_myName.setFocusableInTouchMode(false);
            tv_complete.setVisibility(View.INVISIBLE);
        }
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myHead:
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, 1);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
            case R.id.myName:
                break;
            case R.id.myBirth:
                birthday = tv_myBirth.getText().toString();
                getDate(birthday);
                birthDialog.show();
                break;
            case R.id.tv_complete:
                userName=tv_myName.getText().toString();
                birthday = tv_myBirth.getText().toString();
                if(et_search.getText().toString().trim().length() != 11 || !isNumeric(et_search.getText().toString().trim())) {
                    upload_birth(user_id, birthday);
                    upload_name(user_id, userName);

                    dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                    dialog.setSuccessful("修改成功！");
                    dialog.show();
                    handler.sendEmptyMessageDelayed(CLOSE, 2000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(ModifyInfoActivity.this, FragmentActivity.class);
                            startActivity(intent1);
                        }
                    }, 2000);

                    break;
                }else {
                    TransToUU(friend);
//                    upload_birth(user_id, birthday);
//                    upload_name(user_id, userName);
                    // Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);

                    dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                    dialog.setSuccessful("关联成功！");
                    dialog.show();
                    handler.sendEmptyMessageDelayed(CLOSE, 2000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(ModifyInfoActivity.this, FragmentActivity.class);
                            startActivity(intent1);
                        }
                    }, 2000);

                    break;
                }
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
                    FindFriend(phone);
                    break;
                }
        }
    }
    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month,day;
            if((++monthOfYear)<9){
                month ="0"+monthOfYear;
            }else{
                month =monthOfYear+"";
            }
            if(dayOfMonth<9){
                day ="0"+dayOfMonth;
            }else{
                day =dayOfMonth+"";
            }
            tv_myBirth.setText(year+"-"+month+"-"+day);
        }
    };

    private void getDate(String birth){//获取生日信息
        if(!birth.equals("")){
            year = Integer.parseInt(birth.substring(0,4));
            month = Integer.parseInt(birth.substring(5,birth.indexOf('-',5)))-1;
            day = Integer.parseInt(birth.substring(birth.indexOf('-',5)+1,birth.length()));
        }else {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }
    //TODO *点击头像区域换图片来预览
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            mpath =  getPath(this,uri);

            Log.v("key","选择的图片路径："+mpath);
            upload_headImage(user_virtual.getObjectId());

            ContentResolver cr = this.getContentResolver();
            // Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            Bitmap bitmap = BitmapFactory.decodeFile(mpath);
            /* 将Bitmap设定到ImageView */
            head_image.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //TODO *更新虚拟用户头像
    private void upload_headImage(final String u_v_id){
        Log.v("key","准备上传的图片路径："+mpath);
        final BmobFile bmobFile = new BmobFile(new File(mpath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    saveFile(u_v_id,bmobFile);
                } else {
                    Log.v("error","上传图片失败："+e.getMessage());
                }
            }
        });
    }


    private void saveFile(String id,BmobFile file){
        User_virtual user_virtual = new User_virtual();
        user_virtual.setU_pic_url(file);
        user_virtual.update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.v("key","User_virtual表关联图片成功！");
                }else{
                    Log.v("error","User_virtual表关联图片失败："+e.getMessage());
                }
            }
        });
    }
    //TODO *更新虚拟用户昵称
    private void upload_name(String u_v_id,String name){
        User_virtual userVirtual = new User_virtual();
        userVirtual.setU_virtual_name(name);
        userVirtual.update(u_v_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("key","更新昵称成功！");
                }else{
                    Log.i("error","更新昵称失败："+e.getMessage());
                }
            }
        });
    }
    //TODO *更新虚拟用户出生日期
    private void upload_birth(String u_v_id,String birth){
        User_virtual userVirtual = new User_virtual();
        BmobDate time = BmobDate.createBmobDate("yyyy-MM-dd",birth);
        userVirtual.setU_birth(time);
        userVirtual.update(u_v_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","更新生日成功");
                }else{
                    Log.i("bmob","更新生日失败："+e.getMessage());
                }
            }
        });
    }
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(),null);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = et_search.getText().toString();
        if(content.equals("")){
            tv_searchPeople.setVisibility(View.GONE);
        }else {
            tv_searchPeople.setVisibility(View.VISIBLE);

            tv_searchPeople.setText("找朋友：" + content);

        }
    }
    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
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

    protected void FindFriend(String phone){
        BmobQuery<User> bmobQuery0 = new BmobQuery<>();
        bmobQuery0.addWhereEqualTo("username", phone);
        bmobQuery0.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null){
                    Log.i("Friend","成功找到friend");

                    dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                    dialog.setSuccessful("已找到该好友！\n请点击完成来实现好友关联！");
                    dialog.show();
                    handler.sendEmptyMessageDelayed(CLOSE, 3000);

                    friend = list.get(0);
                    et_search.setText(et_search.getText());
                }else
                    Log.i("Friend","没能成功找到Friend");
            }
        });
    }

    protected void TransToUU(final User friend){

        BmobQuery<Link_user_virtual> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_virtual_id", user_virtual.getObjectId());
        bmobQuery.findObjects(new FindListener<Link_user_virtual>() {
            @Override //TODO 已经获取到了L_UV表的record->进行转移
            public void done(List<Link_user_virtual> list, BmobException e) {
                if(e == null){
                    Log.i("L_UV_record", "成功找到L_UV_record，正准备转移");
                    List<String> list2 = list.get(0).getL_record_id();
                    //TODO 创建L_UU表中内容
                    Link_user2 link_user2 = new Link_user2();
                    BmobUser user = BmobUser.getCurrentUser(User.class);
                    link_user2.setL_user_id(user.getObjectId());
                    link_user2.setL_linked_user_id(friend.getObjectId());//输入的好友的objectId
                    link_user2.setL_record_id(list2);
                    link_user2.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("L_UU_record","成功保存record入L_UU表中");
                                dele_virtual_friend();
                            }else{
                                Log.i("L_UU_record","未能成功保存record进L_UU表中");
                            }
                        }
                    });
                }else{
                    Log.i("L_UV_record", "没能在L_UV表中找到，无法转移record");
                }
            }
        });
    }
    protected void dele_virtual_friend(){
        BmobQuery<Link_user_virtual> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_virtual_id", user_virtual.getObjectId());
        bmobQuery.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> list, BmobException e) {
                Link_user_virtual virtual_firend = list.get(0);

                virtual_firend.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null)
                            Log.i("Friend_L","删除虚拟好友空间成功");
                        else
                            Log.i("Friend_L", "删除虚拟好友空间失败");
                    }
                });
            }
        });
        User_virtual user_virtual_firend = new User_virtual();
        user_virtual_firend.setObjectId(user_virtual.getObjectId());
        user_virtual_firend.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null)
                    Log.i("Friend", "虚拟好友删除成功");
                else
                    Log.i("Friend", "虚拟好友删除失败");
            }
        });
    }


}
