package com.rentalphang.runj.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * 评论实体类
 *
 * Created by 洋 on 2016/5/10.
 */
public class Comment  extends BmobObject{

    private Dynamic dynamic; //动态id

    private User fromUser; //评论者

    private User toUser; //被评论者

    private String content; //内容

    public Dynamic getDynamic() {
        return dynamic;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public String getContent() {
        return content;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
}
