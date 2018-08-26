package com.roadeed.sh;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.roadeed.sh.models.Video;
import com.roadeed.sh.utils.Constant;

public class VideoBubbleService extends Service {
    private WindowManager windowManager;
    private VideoManager videoManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Video video = intent.getParcelableExtra( Video.KEY );
        String vide_id=intent.getStringExtra(Constant.KEY_VIDEO_ID);

       // if( video != null ){
            Log.d("vide_id",vide_id);
            videoManager = getVideoManager();
            videoManager.updateViewRoot(vide_id);
      //  }
        return Service.START_REDELIVER_INTENT;
    }

    private VideoManager getVideoManager(){
        if( videoManager == null ){
            newVideoManager();
        }
        return videoManager;
    }

    private void newVideoManager(){
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate( R.layout.video_float_window, null, false );
        videoManager = new VideoManager( windowManager, layout );
        windowManager.addView( videoManager.getViewRoot(), videoManager.getParams() );



    }








    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView( videoManager.getViewRoot() );

    }
}


