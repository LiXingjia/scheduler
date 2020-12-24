package com.in.timelinenested.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by bobee on 2019/3/2.
 *
 */

public class User_virtual extends BmobObject {

    private String U_virtual_name;
    private BmobDate U_birth;
    private BmobFile U_pic_url;

    public String getU_virtual_name() {
        return U_virtual_name;
    }

    public void setU_virtual_name(String u_virtual_name) {
        U_virtual_name = u_virtual_name;
    }

    public BmobDate getU_birth() {
        return U_birth;
    }

    public void setU_birth(BmobDate u_birth) {
        U_birth = u_birth;
    }

    public BmobFile getU_pic_url() {
        return U_pic_url;
    }

    public void setU_pic_url(BmobFile u_pic_url) {
        U_pic_url = u_pic_url;
    }
}
