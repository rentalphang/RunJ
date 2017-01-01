package com.rentalphang.runj.model.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 *
 * 动态实体类
 *
 * Created by 洋 on 2016/5/10.
 */
public class Dynamic extends BmobObject {

    private User fromUser; //发布者

    private String theme; //主题

    private String content; //内容

    private List<String> image; //图片url集合

    private BmobRelation likes; //点赞人集合

    private Integer likesCount; //点赞数

    private Integer commentCount; //评论数

    public User getFromUser() {
        return fromUser;
    }

    public String getTheme() {
        return theme;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImage() {
        return image;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getLikesCount() {

        return likesCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Dynamic) {
            Dynamic d = (Dynamic)o;

            return this.getObjectId().equals(d.getObjectId());
        }
        return super.equals(o);
    }
}
