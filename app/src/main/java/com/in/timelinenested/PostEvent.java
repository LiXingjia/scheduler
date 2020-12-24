package com.in.timelinenested;

import java.io.Serializable;

/**
 * 消息分发载体
 * Created by wgy on 16/10/17.
 */
public class PostEvent implements Serializable {

    public int what = 0;
    public Object object ;

    public PostEvent() {
    }
    public PostEvent(Object object) {
        this.object = object;
    }
}
