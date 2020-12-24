package com.in.timelinenested;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.socket.RegisterSocket;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
    private Button btn_register, btn_verify;
    private EditText et_password,et_phone,et_verify,et_passwordCheck;
    private TextView tv_phoneMis,tv_passwordMis,tv_checkMis;
    private ConstraintLayout layout_register;
    private String phone;
    private String verifycode = "";
    SharedPreferences sps = null;
    SharedPreferences.Editor editor = null;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        viewInit();
        et_phone.setText("");

    }
    private void viewInit(){
        layout_register = findViewById(R.id.layout_register);
        layout_register.setOnClickListener(this);

        btn_register = findViewById(R.id.btn_register);
        btn_verify = findViewById(R.id.btn_verify);
        btn_register.setOnClickListener(this);
        btn_verify.setOnClickListener(this);

        et_password = findViewById(R.id.et_password);
        et_phone = findViewById(R.id.et_phone);
        et_passwordCheck = findViewById(R.id.et_passwordCheck);
        et_verify = findViewById(R.id.et_verify);
        et_phone.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        et_passwordCheck.addTextChangedListener(this);
        et_verify.addTextChangedListener(this);
        et_phone.setOnFocusChangeListener(this);
        et_password.setOnFocusChangeListener(this);
        et_passwordCheck.setOnFocusChangeListener(this);

        tv_phoneMis = findViewById(R.id.tv_phoneMis);
        tv_passwordMis = findViewById(R.id.tv_passwordMis);
        tv_checkMis = findViewById(R.id.tv_checkMis);




    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_register:
                v.requestFocus();
                v.requestFocusFromTouch();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                break;
            case R.id.btn_verify:
                equalPhone();
                break;
            case R.id.btn_register:
                sureSMS();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        String phone = et_phone.getText().toString();
        String password = et_password.getText().toString();
        String passwordCheck = et_passwordCheck.getText().toString();
        switch (view.getId()) {
            case R.id.et_phone:
                if (!focused && phone.length() != 11) {
                    tv_phoneMis.setText("请输入正确的账号密码");
                    tv_phoneMis.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tv_phoneMis.setText("");
                }
                break;
            case R.id.et_password:
                if (!focused && password.equals("")) {
                    tv_passwordMis.setText("密码不能为空");
                    tv_passwordMis.setTextColor(getResources().getColor(R.color.red));
                } else if (!focused && password.length() < 6) {
                    tv_passwordMis.setText("密码至少为6位");
                    tv_passwordMis.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tv_passwordMis.setText("");
                }
                break;
            case R.id.et_passwordCheck:
                if (!focused && !passwordCheck.equals(password)) {
                    tv_checkMis.setText("两次密码输入不一致");
                    tv_checkMis.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tv_checkMis.setText("");
                }
                break;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void afterTextChanged(Editable editable) {
        phone = et_phone.getText().toString();
        String password = et_password.getText().toString();
        String password2 = et_passwordCheck.getText().toString();
        String verify = et_verify.getText().toString();
        if(phone.length() != 11 || password.length()<6 ||
                !password2.equals(password) || verify.length() !=6){
            btn_register.setEnabled(false);
        }else{
            btn_register.setEnabled(true);
        }
    }
    //发送验证码
    private void sendSMS(){
        BmobSMS.requestSMSCode(et_phone.getText().toString(),
                "", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer smsId, BmobException e) {
                        if (e == null) {
                            Log.v("Key","发送验证码成功，短信ID：" + smsId + "\n");
                        } else {
                            Log.v("Key","发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                        }
                    }
                });
    }
    //检查手机号是否已经注册
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
                        sendSMS();
                    }else{
                        Toast.makeText(RegisterActivity.this,"手机号已经注册!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("查询失败！", e.getMessage());

                }
            }
        });
    }

    //注册
    private void signUp() {
        final User user = new User();
        //此处差一个将号码设置成用户的昵称
        user.setUsername(et_phone.getText().toString());
        user.setPassword(et_password.getText().toString());
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    user.getObjectId();
                    Link_user link_user = new Link_user();
                    link_user.setL_user_id(user.getObjectId());
                    link_user.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {

                        }
                    });
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                } else {
                    Log.v("Key",e.getMessage());
                }
            }
        });
    }
    //检验验证码输入是否正确
    private  void sureSMS(){
        BmobSMS.verifySmsCode(et_phone.getText().toString(),et_verify.getText().toString(),
                new UpdateListener() {
                    @Override
                    public void done(BmobException ex) {
                        if (ex==null){
                            //Toast.makeText(RegisterActivity.this,"",Toast.LENGTH_LONG).show();
                            signUp();
                            startActivity(new Intent(RegisterActivity.this, LogActivity.class));
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"验证码输入错误！",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
