package com.roadeed.sh.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/*
public class JsonCatgory {

    @SerializedName("cat")
    @Expose
    private List<Cat> cat = null;

    public List<Cat> getCat() {
        return cat;
    }

    public void setCat(List<Cat> cat) {
        this.cat = cat;
    }

}*/


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonCatgory {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}



