package com.in.timelinenested;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.in.timelinenested.SetUpActivity.getPath;

public class MyModifyinfoActivity extends AppCompatActivity implements View.OnClickListener  {

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
    User mUser;
    String TAG="Mymodify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.head_info);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }

        init();
    }
    private void init(){
        context = this;
        mUser = BmobUser.getCurrentUser(User.class);

        Log.i(TAG, "muser "+mUser);
        Log.i(TAG, "id "+mUser.getObjectId());
        Log.i(TAG, "init: "+mUser.getU_pic_url());

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
        connect.setVisibility(View.INVISIBLE);//关联键不可见
        //获取头像
        final String IMAGE_URL;




       IMAGE_URL= mUser.getU_pic_url().getFileUrl();
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

        userName=mUser.getNickname();
        long t = BmobDate.getTimeStamp(mUser.getU_birth().getDate());
        birthday= getDateToString(t,"yyyy-MM-dd");

        tv_myName.setText(userName);
        tv_myBirth.setText(birthday);
        getDate(birthday);//把生日设在滑轮上
        birthDialog = new DatePickerDialog(context,DatePickerDialog.THEME_HOLO_LIGHT,datePickerListener,year,month,day);

        myHead.setOnClickListener(this);
        myName.setOnClickListener(this);
        myBirth.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.myHead:
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
             //   birthday = tv_myBirth.getText().toString();
                break;
            case R.id.tv_complete:
                userName=tv_myName.getText().toString();
                birthday = tv_myBirth.getText().toString();
      //          Log.i(TAG, "onClick: name"+userName);
     //           Log.i(TAG, "onClick:before "+birthday);
                upload_birth(mUser.getObjectId(),birthday);
      //          Log.i(TAG, "onClick:after "+birthday);
                upload_name(mUser.getObjectId(),userName);
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();

                break;
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
    //TODO *点击头像区域换图片来预览
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();

            mpath =  getPath(this,uri);
            Log.v("key","选择的图片路径："+mpath);
            upload_headImage(mUser.getObjectId(),mpath);

            Bitmap bitmap = BitmapFactory.decodeFile(mpath);
            /* 将Bitmap设定到ImageView */
            head_image.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //TODO *更新虚拟用户头像
    private void upload_headImage(final String user_id,String picture){
        Log.v("key","准备上传的图片路径："+picture);
        final BmobFile bmobFile = new BmobFile(new File(picture));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    saveFile(user_id,bmobFile);
                } else {
                    Log.v("error","上传图片失败："+e.getMessage());
                }
            }
        });
    }
    private void saveFile(String id,BmobFile file){
        User user = new User();
        user.setU_pic_url(file);
        user.update(id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    setResult(Activity.RESULT_OK);
                    finish();
                    Log.v("key","User_virtual表关联图片成功！");
                }else{
                    Log.v("error","User_virtual表关联图片失败："+e.getMessage());
                }
            }
        });
    }
    //TODO *更新虚拟用户昵称
    private void upload_name(String u_v_id,String name){
        User user = new User();
        user.setNickname(name);
        user.update(u_v_id, new UpdateListener() {
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
        User user = new User();
        BmobDate time = BmobDate.createBmobDate("yyyy-MM-dd",birth);
        user.setU_birth(time);
        user.update(u_v_id, new UpdateListener() {
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



}
