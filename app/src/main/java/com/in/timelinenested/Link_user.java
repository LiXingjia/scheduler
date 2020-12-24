package com.in.timelinenested;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Dsl on 2019/3/2.
 */

public class Link_user extends BmobObject {
    private String L_user_id;//用户的空间写记录
    private List<String> L_record_id;  //用户的空间写记录

    public List<String> getL_record_id() {
        return L_record_id;
    }

    public void setL_record_id(List<String> l_record_id) {
        L_record_id = l_record_id;
    }

    public String getL_user_id() {
        return L_user_id;
    }

    public void setL_user_id(String l_user_id) {
        L_user_id = l_user_id;
    }
}
