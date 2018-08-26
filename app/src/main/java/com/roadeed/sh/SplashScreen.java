package com.roadeed.sh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.common.util.IOUtils;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.utils.Constant;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private GifImageView gifImageView;
    private ProgressBar progressBar;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        gifImageView = (GifImageView)findViewById(R.id.gifImageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(progressBar.VISIBLE);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
        setTaskBarColored(SplashScreen.this);

        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImageView);

        /*new GifDataDownloader() {
            @Override
            protected void onPostExecute(final byte[] bytes) {
                gifImageView.setBytes(bytes);
                gifImageView.startAnimation();
                Log.d(TAG, "GIF width is " + gifImageView.getGifWidth());
                Log.d(TAG, "GIF height is " + gifImageView.getGifHeight());
            }
        }.execute("http://sharq1.com/1.gif");*/


        //Set GIFImageView resource
        try{

            InputStream inputStream = getAssets().open("splash1.gif");

            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();

        }
        catch (IOException ex)
        {

        }


        //Wait for 3 seconds and start Activity Main
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this,MainActivity.class));
                SplashScreen.this.finish();
            }
        },3000); // 3000 = 3seconds
    }


    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static void setTaskBarColored(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(Constant.color);
        }
    }



}
