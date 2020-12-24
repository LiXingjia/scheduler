package com.in.timelinenested;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Dsl on 2019/3/2.
 */

public class Link_user2 extends BmobObject {
    private String L_user_id;
    private String L_linked_user_id;
    private List<String> L_record_id;
    private Boolean L_black;

    public String getL_user_id() {
        return L_user_id;
    }

    public void setL_user_id(String l_user_id) {
        L_user_id = l_user_id;
    }

    public String getL_linked_user_id() {
        return L_linked_user_id;
    }
    public Boolean getL_black() {
        return L_black;
    }

    public void setL_linked_user_id(String l_linked_user_id) {
        L_linked_user_id = l_linked_user_id;
    }

    public List<String> getL_record_id() {
        return L_record_id;
    }
    public void setL_black(Boolean l_black) {
        L_black = l_black;
    }

    public void setL_record_id(List<String> l_record_id) {
        L_record_id = l_record_id;
    }
}
