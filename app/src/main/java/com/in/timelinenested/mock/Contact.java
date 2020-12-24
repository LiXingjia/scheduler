package com.in.timelinenested.mock;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.in.timelinenested.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Contact implements Serializable {

    private Drawable mProfileImage;
    private String mFirstName;
    private String userId;
    private String phone;
    private String sex;
    private String birthday;
    int flag=0;//0是虚拟朋友，1是真实好友


    public Drawable getProfileImage() {
        return mProfileImage;
    }

    public String getFirstName() {
        return mFirstName;
    }
    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setmProfileImage(Drawable mProfileImage) {
        this.mProfileImage = mProfileImage;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    //    public static List<Contact> mocks(Context c) {
//        List<Contact> contacts = new ArrayList<>(60);
//
//        contacts.add(fromRes(c, R.drawable.pic_01, R.string.fn_m_01));
//        contacts.add(fromRes(c, R.drawable.pic_02, R.string.fn_m_02));
//        contacts.add(fromRes(c, R.drawable.pic_03, R.string.fn_m_03));
//        contacts.add(fromRes(c, R.drawable.pic_04, R.string.fn_m_04));
//        contacts.add(fromRes(c, R.drawable.pic_05, R.string.fn_m_05));
//        contacts.add(fromRes(c, R.drawable.pic_06, R.string.fn_m_06));
//        contacts.add(fromRes(c, R.drawable.pic_07, R.string.fn_m_07));
//        contacts.add(fromRes(c, R.drawable.pic_08, R.string.fn_m_08));
//        contacts.add(fromRes(c, R.drawable.pic_09, R.string.fn_m_09));
//        contacts.add(fromRes(c, R.drawable.pic_10, R.string.fn_m_10));
//        contacts.add(fromRes(c, R.drawable.pic_11, R.string.fn_m_11));
//        contacts.add(fromRes(c, R.drawable.pic_12, R.string.fn_m_12));
//        contacts.add(fromRes(c, R.drawable.pic_13, R.string.fn_m_13));
//        contacts.add(fromRes(c, R.drawable.pic_14, R.string.fn_m_14));
//        contacts.add(fromRes(c, R.drawable.pic_15, R.string.fn_m_15));
//        contacts.add(fromRes(c, R.drawable.pic_16, R.string.fn_m_16));
//        contacts.add(fromRes(c, R.drawable.pic_17, R.string.fn_m_17));
//        contacts.add(fromRes(c, R.drawable.pic_18, R.string.fn_m_18));
//        contacts.add(fromRes(c, R.drawable.pic_19, R.string.fn_m_19));
//        contacts.add(fromRes(c, R.drawable.pic_20, R.string.fn_m_20));
//        contacts.add(fromRes(c, R.drawable.pic_21, R.string.fn_m_21));
//        contacts.add(fromRes(c, R.drawable.pic_22, R.string.fn_m_22));
//        contacts.add(fromRes(c, R.drawable.pic_23, R.string.fn_m_23));
//        contacts.add(fromRes(c, R.drawable.pic_24, R.string.fn_m_24));
//        contacts.add(fromRes(c, R.drawable.pic_25, R.string.fn_m_25));
//        contacts.add(fromRes(c, R.drawable.pic_26, R.string.fn_m_26));
//        contacts.add(fromRes(c, R.drawable.pic_27, R.string.fn_m_27));
//        contacts.add(fromRes(c, R.drawable.pic_28, R.string.fn_m_28));
//        contacts.add(fromRes(c, R.drawable.pic_29, R.string.fn_m_29));
//        contacts.add(fromRes(c, R.drawable.pic_30, R.string.fn_m_30));
//
//
//        Collections.sort(contacts, COMPARATOR);
//        return contacts;
//    }

    public static final Comparator<Contact> COMPARATOR = new Comparator<Contact>() {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getFirstName().compareTo(o2.getFirstName());
        }
    };

//    private static Contact fromRes(Context c, @DrawableRes int img, @StringRes int fn) {
//        return new Contact(ContextCompat.getDrawable(c, img), c.getString(fn));
//    }
}
