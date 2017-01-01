package com.rentalphang.runj.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 洋 on 2016/6/12.
 */
public class Like extends BmobObject {
    private User fromUser;
    private Dynamic toDynamic;

    public User getFromUser() {
        return fromUser;
    }

    public Dynamic getToDynamic() {
        return toDynamic;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setToDynamic(Dynamic toDynamic) {
        this.toDynamic = toDynamic;
    }
}
