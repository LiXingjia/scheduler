package com.in.timelinenested;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;


import org.litepal.LitePal;

import cn.bmob.v3.Bmob;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        Bmob.initialize(this, "0bc42a56794a18663c17d2e5e0e384b2");


    }
}

