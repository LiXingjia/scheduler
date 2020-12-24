package com.in.timelinenested;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Dsl on 2019/3/11.
 */

public class tempUser extends BmobObject {
    private String userId;
    private String name;
    private BmobDate birth;
    private BmobFile pic;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobDate getBirth() {
        return birth;
    }

    public void setBirth(BmobDate birth) {
        this.birth = birth;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
}
