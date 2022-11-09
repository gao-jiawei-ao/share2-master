package com.example.myapplication3.entity;

import java.io.Serializable;

public class PictureEntity implements Serializable {

    private String imageCode;
    private String imageUrlList;
    private String id;
    private String likeNum;
    private String title;
    private String content;
    private String pUserId;
    private String username;
    private String likeId;
    private String collectId;
    private String createTime;
    private boolean hasLike;

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(String imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getpUserId() {
        return pUserId;
    }

    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isHasLike() {
        return hasLike;
    }

    @Override
    public String toString() {
        return "PictureEntity{" +
                "imageCode='" + imageCode + '\'' +
                ", imageUrlList='" + imageUrlList + '\'' +
                ", id='" + id + '\'' +
                ", likeNum='" + likeNum + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", pUserId='" + pUserId + '\'' +
                ", username='" + username + '\'' +
                ", likeId='" + likeId + '\'' +
                ", collectId='" + collectId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", hasLike=" + hasLike +
                '}';
    }

    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }
}
