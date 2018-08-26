package com.roadeed.sh.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

    private static Context mContext;
    Activity activity;
    private RequestQueue requestQueue;
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();

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

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private RequestQueue getRequestQueue(){
        return  requestQueue;
    }

    public void cancelRequest(String tag){
        getRequestQueue().cancelAll(tag);
    }



    private static Activity mCurrentActivity = null;
    public static Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }



    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public static Context getAppContext() {
        return mContext;
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

}




