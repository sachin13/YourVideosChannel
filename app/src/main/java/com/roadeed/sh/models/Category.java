package com.roadeed.sh.models;

import java.io.Serializable;

public class Category implements Serializable {

    public int cid = -1;
    public Integer cat = -1;
    public String category_name = "";
    public String category_image = "";
    public String video_count = "";
    public boolean check_notification= true;

    public Category(int cid, String category_name, String category_image, String video_count, boolean check_notification) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.video_count = video_count;
        this.check_notification = check_notification;
    }

    public Category(int cid,Integer cat, String category_name, String category_image, String video_count, boolean check_notification) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.video_count = video_count;
        this.check_notification = check_notification;
        this.cat =cat;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public Integer getCat() {
        return cat;
    }

    public void setCat(Integer cat) {
        this.cat = cat;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
    }

    public boolean isCheck_notification() {
        return check_notification;
    }

    public void setCheck_notification(boolean check_notification) {
        this.check_notification = check_notification;
    }
}
/*
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("cid")
    @Expose
    private Integer cid;
    @SerializedName("cat")
    @Expose
    private Object cat;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_image")
    @Expose
    private String categoryImage;
    @SerializedName("video_count")
    @Expose
    private Integer videoCount;

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Object getCat() {
        return cat;
    }

    public void setCat(Object cat) {
        this.cat = cat;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

}
*/