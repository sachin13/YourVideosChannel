package com.roadeed.sh.callbacks;

import com.roadeed.sh.models.Category;
import com.roadeed.sh.models.Video;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Video> posts = new ArrayList<>();

}
