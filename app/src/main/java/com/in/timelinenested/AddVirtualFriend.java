package com.in.timelinenested;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.activity.FragmentActivity;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.mock.Contact;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;
import static com.in.timelinenested.SetUpActivity.getPath;

public class AddVirtualFriend extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private TextView tv_back;
    private LinearLayout myHead,myName,myBirth;
    private TextView tv_myBirth;
    private EditText tv_myName;
    private int year,month,day;
    private DatePickerDialog birthDialog;
    private SharedPreferences sps = null;
    private String userHead,userName,birthday;
    private String[] userInfo = new String[10];
    private ImageView head_image;
    private String mpath;
    private User_virtual return_u_v;

    private com.in.timelinenested.viewClass.LoadingDialog dialog;

    private Button bt_addSure;

    Contact contact;
    User user;
    User_virtual user_virtual;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_virtual_friend);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        init();
    }

    private void init(){
        //获取好友信息
     //   Intent intent=getIntent();
      //  Bundle bundleM = intent.getBundleExtra("data");
        //TODO 需要判断是虚拟用户还是真实用户的ID
     //   user_id=(String) bundleM.getSerializable("user_id");
//        if(bundleM==null){
//            contact=new Contact();//创建虚拟用户,此时contact不为null
//        }else {
//            contact = (Contact) bundleM.getSerializable("contact");
//        }

        context = this;
        //contact=new Contact();

        tv_back = findViewById(R.id.tv_back);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.back);
        //       drawable.setBounds(0,0, ImageUtil.dp2px(context,20), ImageUtil.dp2px(context,20));
        tv_back.setCompoundDrawables(drawable,null,null,null);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        myHead = findViewById(R.id.myHead);
        myName = findViewById(R.id.myName);

        myBirth = findViewById(R.id.myBirth);
        tv_myName = findViewById(R.id.tv_myName);

        tv_myBirth = findViewById(R.id.tv_myBirth);
        head_image=findViewById(R.id.head_image);

        bt_addSure = findViewById(R.id.bt_addSure);

        getDate(birthday);
        birthDialog = new DatePickerDialog(context,DatePickerDialog.THEME_HOLO_LIGHT,datePickerListener,year,month,day);

        myHead.setOnClickListener(this);
        myName.setOnClickListener(this);
        myBirth.setOnClickListener(this);
        bt_addSure.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.myHead:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            case R.id.myName:
                break;
            case R.id.myBirth:
                getDate(birthday);
                birthDialog.show();
                break;
            case R.id.bt_addSure:
                //               contact=new Contact(R.drawable.pic_02,tv_myName.getText().toString());
                /*String[] infoArr = getInfoArr();
                new InfoUpdateSocket().setInfo(userId,infoArr);*/
//                contact.setBirthday(birthday);
//                contact.setmFirstName(userName);
                //导入数据库
//                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_OK);
                userName = tv_myName.getText().toString();
                birthday = tv_myBirth.getText().toString();

                addUserVirtual(userHead,userName,birthday);
               // finish();
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
    private void getDate(String birth){//获取生日信息
//        if(!birth.equals("")){
//            year = Integer.parseInt(birth.substring(0,4));
//            month = Integer.parseInt(birth.substring(5,birth.indexOf('-',5)))-1;
//            day = Integer.parseInt(birth.substring(birth.indexOf('-',5)+1,birth.length()));
//        }else {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
//        }
    }
    private String[] getInfoArr(){
        userInfo[0]= tv_myName.getText().toString();
        userInfo[3]= tv_myBirth.getText().toString();

        SharedPreferences.Editor editor = sps.edit();
        editor.putString("userName",userInfo[0]);
        editor.putString("birthday",userInfo[3]);
        editor.apply();
        return userInfo;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode==1 && data!=null){
            Uri uri = data.getData();

            userHead =  getPath(this,uri);

            Log.v("key","选择的图片路径："+ userHead);
            //upload_headImage("76f769f475");//TODO 从前端拿选中好友ID

            // ContentResolver cr = this.getContentResolver();
            //                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            Bitmap bitmap = BitmapFactory.decodeFile(userHead);
            /* 将Bitmap设定到ImageView */
            head_image.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //TODO *添加虚拟用户
    //TODO 1、上传头像到云端素材—>返回给前端的上传文件的完整地址
    private void addUserVirtual(String  picPath, final String name, String date) {//传入头像的本地路径、昵称、生日

        Log.v("key","准备上传的图片路径："+picPath);
       // picPath = "/storage/emulated/0/" + picPath ;
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        final BmobDate time = BmobDate.createBmobDate("yyyy-MM-dd",date);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    saveMessage(name,time,bmobFile);
                    // u_v_picUrl = bmobFile.getFileUrl();
                    dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                    dialog.setSuccessful("虚拟好友添加成功！");
                    dialog.show();
                    handler.sendEmptyMessageDelayed(CLOSE, 2000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(AddVirtualFriend.this,FragmentActivity.class);
                            startActivity(intent1);
                        }
                    }, 2000);

                } else {
                    Log.v("error","上传图片失败："+e.getMessage());
                }
            }
        });
    }

    //TODO 添加虚拟用户
    //TODO 2、添加虚拟用户数据：昵称、生日、头像—>返回给前端的添加的虚拟用户的ID
    private void saveMessage(String name,BmobDate birth,BmobFile file){
        final BmobFile mfile = file;
        final User_virtual userVirtual = new User_virtual();
        //注意：不能调用setObjectId("")方法
        userVirtual.setU_virtual_name(name);
        userVirtual.setU_birth(birth);
        userVirtual.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    userVirtual.setObjectId(objectId);
                    return_u_v = userVirtual;//给前端的添加的虚拟用户实体类！
                    saveFile(objectId,mfile);
                    setU_V_id(objectId);
                }else{
                    Log.i("error","在User_virtual表中添加单条数据失败："+e.getMessage());
                }
            }
        });
    }

    //TODO 添加虚拟用户
    //TODO 3、上传的头像与虚拟用户表的U_pic_url关联
    private void saveFile(String id,BmobFile file){
        User_virtual user = new User_virtual();
        user.setU_pic_url(file);
        user.update(id, new UpdateListener() {
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

    //TODO 添加虚拟用户
    //TODO 4、在Link_user_virtual表中添加一条记录
    private void setU_V_id(final String u_v_id){
        Link_user_virtual link_user_virtual = new Link_user_virtual();
        User user = BmobUser.getCurrentUser(User.class);
        link_user_virtual.setL_user_id(user.getObjectId());
        link_user_virtual.setL_virtual_id(u_v_id);
        link_user_virtual.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.i("key","在Link_user_virtual表中添加一条记录成功！");
                }else{
                    Log.i("error","在Link_user_virtual表中添加一条记录失败:"+e.getMessage());
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
