package com.in.timelinenested;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Dsl on 2019/3/4.
 */

public class Record extends BmobObject {
    private String R_title;
    private String R_text;
    private BmobDate R_date;
    private String R_author;
    private Boolean R_read;
 //   private BmobFile R_pic_url;

    public String getR_title() {
        return R_title;
    }

    public void setR_title(String r_title) {
        R_title = r_title;
    }

    public String getR_text() {
        return R_text;
    }

    public void setR_text(String r_text) {
        R_text = r_text;
    }

//    public BmobFile getR_pic_url() {
//        return R_pic_url;
//    }
//
//    public void setR_pic_url(BmobFile r_pic_url) {
//        R_pic_url = r_pic_url;
//    }

    public BmobDate getR_date() {
        return R_date;
    }

    public void setR_date(BmobDate r_date) {
        R_date = r_date;
    }

    public String getR_author() {
        return R_author;
    }

    public void setR_author(String r_author) {
        R_author = r_author;
    }

    public Boolean getR_read() {
        return R_read;
    }

    public void setR_read(Boolean r_read) {
        R_read = r_read;
    }
}
