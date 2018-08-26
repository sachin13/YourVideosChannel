package com.roadeed.sh.models;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by hp on 12/09/2017.
 */

public class VolleyRequests<T> extends Observable {
    public interface IReceiveData<T> {
        void onDataReceived(T posts);
    }

    IReceiveData iReceiveData;

    public VolleyRequests setIReceiveData(IReceiveData iReceiveData) {
        this.iReceiveData = iReceiveData;
        return this;
    }


    public void getCatgoryNotification(String token,String cat,int position){
        final StringRequest jsonObjectRequestcatgoryNotification= new StringRequest(Request.Method.POST, "http://sharq1.com/notificationSettings.php?token="+token+
                "&cat"+position+"="+cat, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                   GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                JsonResult jsonResult = gson.fromJson(response.toString(), com.roadeed.sh.models.JsonResult.class);
                    setChanged();
                notifyObservers(jsonResult);

                if (iReceiveData != null) {
                    iReceiveData.onDataReceived(jsonResult);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        com.roadeed.sh.activities.MyApplication.getInstance().addToRequestQueue(jsonObjectRequestcatgoryNotification);
    }

    public  void getSwitchvalue(String token){

        final StringRequest jsonObjectRequesttokencatgoryNotification= new StringRequest(Request.Method.POST, "http://sharq1.com/switch_value.php?token="+token,
                new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                JsonCatgory jsonResult = gson.fromJson(response.toString(), com.roadeed.sh.models.JsonCatgory.class);
                setChanged();
                notifyObservers(jsonResult);

                if (iReceiveData != null) {
                    iReceiveData.onDataReceived(jsonResult);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        com.roadeed.sh.activities.MyApplication.getInstance().addToRequestQueue(jsonObjectRequesttokencatgoryNotification);


    }

}