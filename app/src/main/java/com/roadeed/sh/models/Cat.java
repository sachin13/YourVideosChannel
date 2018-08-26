package com.roadeed.sh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cat {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("value")
    @Expose
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}