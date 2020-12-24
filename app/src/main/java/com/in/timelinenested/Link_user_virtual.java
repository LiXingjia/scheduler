package com.in.timelinenested;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Dsl on 2019/3/2.
 *
 */

public class Link_user_virtual extends BmobObject {
    private String L_user_id;//添加好友
    private String L_virtual_id;//添加好友
    private List<String> L_record_id;
    private Boolean L_black;

    public String getL_user_id() {
        return L_user_id;
    }

    public void setL_user_id(String l_user_id) {
        L_user_id = l_user_id;
    }

    public String getL_virtual_id() {
        return L_virtual_id;
    }

    public void setL_virtual_id(String l_virtual_id) {
        L_virtual_id = l_virtual_id;
    }

    public List<String> getL_record_id() {
        return L_record_id;
    }

    public void setL_record_id(List<String> l_record_id) {
        L_record_id = l_record_id;
    }

    public Boolean getL_black() {
        return L_black;
    }

    public void setL_black(Boolean l_black) {
        L_black = l_black;
    }
}
