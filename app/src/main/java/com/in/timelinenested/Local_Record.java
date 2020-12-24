package com.in.timelinenested;

import org.litepal.crud.DataSupport;

/**
 * Created by bobee on 2019/3/5.
 * 这是用于什么的模版
 */

public class Local_Record extends DataSupport{

    private String LR_title;
    private String LR_content;
    private String LR_Date;
    private String LR_place;
    private String LR_author;
    private String LR_record_id;
    private Integer LR_date_seq;
    private Boolean LR_read;

    public String getLR_title() {
        return LR_title;
    }

    public void setLR_title(String LR_title) {
        this.LR_title = LR_title;
    }

    public Integer getLR_date_seq() { return LR_date_seq; }

    public void setLR_date_seq(Integer LR_date_seq) { this.LR_date_seq = LR_date_seq; }

    public String getLR_place() { return LR_place; }

    public void setLR_place(String LR_place) { this.LR_place = LR_place; }

    public String getLR_record_id() {return LR_record_id;}

    public void setLR_record_id(String LR_record_id) {this.LR_record_id = LR_record_id;}

    public String getLR_content() {
        return LR_content;
    }

    public void setLR_content(String LR_content) {
        this.LR_content = LR_content;
    }

    public String getLR_Date() {
        return LR_Date;
    }

    public void setLR_Date(String LR_Date) {
        this.LR_Date = LR_Date;
    }

    public String getLR_author() {
        return LR_author;
    }

    public void setLR_author(String LR_author) {
        this.LR_author = LR_author;
    }


    public Boolean getLR_read() {
        return LR_read;
    }

    public void setLR_read(Boolean LR_read) {
        this.LR_read = LR_read;
    }
}
