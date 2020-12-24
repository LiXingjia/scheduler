package com.in.timelinenested;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.bean.User;

import cn.bmob.v3.BmobUser;

/**
 * Created by Dsl on 2019/3/3.
 *
 */


public class Start extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        user = BmobUser.getCurrentUser(User.class);
        handler.sendEmptyMessageDelayed(1, 1000);

    }

    private int defaultSecond = 2;  //显示默认图时间2s

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            defaultSecond--;
            if (defaultSecond == 0) {
                //倒计时完跳进应用
                if (null == user) {
                    //当前没有登录记录
                    startActivity(new Intent(Start.this, LogActivity.class));
                } else {
                    // 已登录，直接进入应用
                    startActivity(new Intent(Start.this, MainActivity.class));
                }

            } else {
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };
}
