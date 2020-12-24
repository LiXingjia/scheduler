package com.in.timelinenested;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Dsl on 2019/3/5.
 */

public class oderRecord extends BmobObject {

    private String oR_text;
    private BmobDate oR_date;
    private String oR_author;
    private Boolean oR_read;
    private String oR_title;
    private String RecordId;

    public String getRecordId() {
        return RecordId;
    }

    public void setRecordId(String recordId) {
        RecordId = recordId;
    }

    public String getoR_text() {
        return oR_text;
    }

    public void setoR_text(String oR_text) {
        this.oR_text = oR_text;
    }

    public BmobDate getoR_date() {
        return oR_date;
    }

    public void setoR_date(BmobDate oR_date) {
        this.oR_date = oR_date;
    }

    public String getoR_author() {
        return oR_author;
    }

    public void setoR_author(String oR_author) {
        this.oR_author = oR_author;
    }

    public Boolean getoR_read() {
        return oR_read;
    }

    public void setoR_read(Boolean oR_read) {
        this.oR_read = oR_read;
    }

    public String getoR_title() {
        return oR_title;
    }

    public void setoR_title(String oR_title) {
        this.oR_title = oR_title;
    }
}
