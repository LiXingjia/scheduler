package com.in.timelinenested;

import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.socket.LoginSocket;
import com.in.timelinenested.viewClass.CircleImageView;
import com.in.timelinenested.viewClass.LoadingDialog;


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;


public class LogActivity extends AppCompatActivity implements TextWatcher{
  //  private static final String TAG = "MainActivity";
    private static final int TIME_OUT = -1;
    private static final int CLOSE = 0;
    private static final int SUCCESS = 1;
    private static final int RESTART = 2;
    private Context context = LogActivity.this;
  //  private String[] sideBarArray = {"激活会员","我的钱包","个性装扮","我的收藏","我的相册","我的文件"};
    private DrawerLayout drawerLayout;
    private EditText et_phone, et_password;
    private TextView tv_new_user, tv_forget, tv_title;
    private ImageView clear_phone,clear_password;
    private ImageView iv_setting,iv_exit;
    private LinearLayout search;
    private Button btn_login;
    private Toolbar toolbar;
    private ListView lv_side;
    protected CircleImageView civ_head,civ_sideBar;
    private ConstraintLayout layout_login;
    private FragmentTabHost mTabHost;
    private LoadingDialog dialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        viewInit();
    }
    //登陆页面视图初始化
    private void viewInit(){
        layout_login = findViewById(R.id.layout_login);
        layout_login.setOnClickListener(new LoginClickListener());

        tv_new_user = findViewById(R.id.tv_new_user);
        tv_forget = findViewById(R.id.tv_forget_password);
        tv_new_user.setOnClickListener(new LoginClickListener());
        tv_forget.setOnClickListener(new LoginClickListener());

        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_phone.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        String phone = et_phone.getText().toString();
        String password = et_password.getText().toString();

        clear_phone = findViewById(R.id.iv_phoneClear);
        clear_password = findViewById(R.id.iv_passwordClear);
        clear_phone.setOnClickListener(new LoginClickListener());
        clear_password.setOnClickListener(new LoginClickListener());
        if(phone.length() != 0){
            clear_phone.setVisibility(View.VISIBLE);
        }else {
            clear_phone.setVisibility(View.INVISIBLE);
        }
        if(password.length() != 0){
            clear_password.setVisibility(View.VISIBLE);
        }else {
            clear_password.setVisibility(View.INVISIBLE);
        }

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new LoginClickListener());
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void afterTextChanged(Editable editable) {
        String phone = et_phone.getText().toString();
        String password = et_password.getText().toString();
        if(phone.length() != 0){
            clear_phone.setVisibility(View.VISIBLE);
        }else {
            clear_phone.setVisibility(View.INVISIBLE);
        }
        if(password.length() != 0){
            clear_password.setVisibility(View.VISIBLE);
        }else {
            clear_password.setVisibility(View.INVISIBLE);
        }

    }

    //登陆页按键监听
    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String phone = et_phone.getText().toString();
            String password = et_password.getText().toString();
            switch (v.getId()) {
                case R.id.layout_login:
                    v.requestFocus();
                    v.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager) LogActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    break;
                case R.id.iv_phoneClear:
                    et_phone.setText("");
                    break;
                case R.id.iv_passwordClear:
                    et_password.setText("");
                    break;
                case R.id.btn_login:
                    if (phone.equals("")) {
                        Toast.makeText(context, "请输入账号密码！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (password.equals("")) {
                        Toast.makeText(context, "请输入密码！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                     else {
                        equalPhone();//验证号码并登陆
                    }
                    break;
                case R.id.tv_new_user:
                    Intent intent = new Intent(context, RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tv_forget_password:
                    break;

            }
        }
    }

    //页面重启函数
    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void equalPhone() {
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("username",et_phone.getText().toString());
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            //object.size()是查询到的数据条数！！！
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    Log.v("Key",""+object.size());
                    if(object.size()==0){
                        Toast.makeText(LogActivity.this,"手机号未注册!",Toast.LENGTH_LONG).show();
                    }else{





                        loginByAccount();
                    }
                } else {
                    Log.e("查询失败！", e.getMessage());

                }
            }
        });
    }


    //登录后进入的第一个界面
    private void loginByAccount() {
        //此处替换为你的用户名密码
        BmobUser.loginByAccount(et_phone.getText().toString(), et_password.getText().toString(), new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Toast.makeText(LogActivity.this,"登录成功！",Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LogActivity.this,MainActivity.class));
                } else {
                    Toast.makeText(LogActivity.this,"密码错误",Toast.LENGTH_LONG).show();
                    Log.i("key", "done: "+e.getMessage());
                }
            }
        });
    }




}
