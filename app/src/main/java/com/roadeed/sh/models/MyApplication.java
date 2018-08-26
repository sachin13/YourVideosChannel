package com.roadeed.sh.models;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hp on 12/09/2017.
 */

public class MyApplication extends Application {

    private RequestQueue requestQueue;
    private static MyApplication anInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        anInstance = this;
    }

    public void addToRequestQueue(Request request){
        if(getRequestQueue() !=null) {
            getRequestQueue().add(request);
        }
        else {
            requestQueue = Volley.newRequestQueue(this);
            getRequestQueue().add(request);
        }
    }

    private RequestQueue getRequestQueue(){
        return  requestQueue;
    }

    public void cancelRequest(String tag){
        getRequestQueue().cancelAll(tag);
    }

    public static synchronized MyApplication getInstance(){
        return anInstance;
    }

    private static Activity mCurrentActivity = null;
    public static Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}
