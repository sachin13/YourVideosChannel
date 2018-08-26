package com.roadeed.sh.rests;

import com.google.firebase.iid.FirebaseInstanceId;
import com.roadeed.sh.Config;
import com.roadeed.sh.callbacks.CallbackCategories;
import com.roadeed.sh.callbacks.CallbackCategoryDetails;
import com.roadeed.sh.callbacks.CallbackListVideo;
import com.roadeed.sh.models.Settings;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: Your Videos Channel";
    String token = Config.TOKEN;
    String API_KEY = Config.API_KEY;

    @Headers({CACHE, AGENT})
    @GET("api/get_posts/?api_key=" + API_KEY)
    Call<CallbackListVideo> getPostByPage(
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_category_index/?api_key=" + API_KEY)
    Call<CallbackCategories> getAllCategories(@Query("token") String token);

    @Headers({CACHE, AGENT})
    @GET("api/get_category_index/?api_key=" + API_KEY)
    Call<CallbackCategories> getAllCategories();

    @Headers({CACHE, AGENT})
    @GET("api/get_category_posts/?api_key=" + API_KEY)
    Call<CallbackCategoryDetails> getCategoryDetailsByPage(
            @Query("id") int id,
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_search_results/?api_key=" + API_KEY)
    Call<CallbackListVideo> getSearchPosts(
            @Query("search") String search,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_privacy_policy/?api_key=" + API_KEY)
    Call<Settings> getPrivacyPolicy();

}
