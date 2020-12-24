package com.in.timelinenested.bean;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.in.timelinenested.mock.Contact;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Dsl on 2019/2/28.
 *
 */

public class User extends BmobUser {
//    private String U_name;
//    private String U_phone;
//    private String U_pw;
    private BmobDate U_birth;
    private Boolean U_sex;
    private BmobFile U_pic_url;
    private String nickname;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BmobDate getU_birth() {
        return U_birth;
    }

    public void setU_birth(BmobDate u_birth) {
        U_birth = u_birth;
    }

    public Boolean getU_sex() {
        return U_sex;
    }

    public void setU_sex(Boolean u_sex) {
        U_sex = u_sex;
    }

    public BmobFile getU_pic_url() {
        return U_pic_url;
    }

    public void setU_pic_url(BmobFile u_pic_url) {
        U_pic_url = u_pic_url;
    }
    public static final Comparator<User> COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getNickname().compareTo(o2.getNickname());
        }
    };


}
