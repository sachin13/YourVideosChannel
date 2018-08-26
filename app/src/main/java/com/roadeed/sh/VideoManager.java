package com.roadeed.sh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.roadeed.sh.activities.ActivityYoutubePlayer;
import com.roadeed.sh.activities.Util;
import com.roadeed.sh.fragments.FragmentUtils;
import com.roadeed.sh.fragments.Yoyubeviewfragment;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.utils.Constant;

public class VideoManager implements View.OnTouchListener {
    private WindowManager windowManager;
    private RelativeLayout viewRoot;
    private WindowManager.LayoutParams params;

    private boolean isClicked = false;
    private boolean isInRightSide = false;
    private int width;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    ImageButton button;
    Context context;
    int LAYOUT_FLAG;

    public VideoManager(WindowManager windowManager, RelativeLayout layout ){
        this.windowManager = windowManager;
        setViewRoot(layout);
        setParams();
        setWidth();
    }

    private void setWidth() {
        Display display = windowManager.getDefaultDisplay();

        if( Util.isPlusEqualsApi13() ){
            Point size = new Point();
            display.getSize( size );
            width = size.x;
        }
        else{
            width = display.getWidth();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch ( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                actionDownUpdate( event );
                return true;
            case MotionEvent.ACTION_UP:
                actionUpUpdate( event );
                return true;
            case MotionEvent.ACTION_MOVE:
                actionMoveUpdate( event );
                return true;
        }

        return false;
    }

    private void actionDownUpdate( MotionEvent event ){
        initialX = params.x;
        initialY = params.y;
        initialTouchX = event.getRawX();
        initialTouchY = event.getRawY();
        isClicked = true;
    }

    private void actionUpUpdate( MotionEvent event ){
        int desiredPosition;
        int posX = params.x + viewRoot.getWidth() / 2;

        if( posX < width / 2 ){
            desiredPosition = 0;
            isInRightSide = false;
        }
        else{
            desiredPosition = width;
            isInRightSide = true;
        }

        slowDrawViewRootMove( desiredPosition );
        callActivityIfClicked();
    }

    private void actionMoveUpdate( MotionEvent event ){
        int extraVal = isInRightSide ? viewRoot.getWidth() * -1 : 0;

        params.x = initialX + extraVal + (int)( event.getRawX() - initialTouchX );
        params.y = initialY + (int)( event.getRawY() - initialTouchY );

        windowManager.updateViewLayout(viewRoot, params );
        isClicked = false;
    }

    private void callActivityIfClicked(){
        if( isClicked ){
            Intent intent = new Intent( viewRoot.getContext(), MainActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            viewRoot.getContext().startActivity(intent);
        }
    }

    private void slowDrawViewRootMove(int desiredPosition ){
        int incDec = params.x < desiredPosition ? 1 : -1;

        while( params.x < desiredPosition
                || params.x > desiredPosition ){

            params.x += incDec;
            windowManager.updateViewLayout(viewRoot, params );
        }
    }

    public RelativeLayout getViewRoot() {

        return viewRoot;
    }

    public void setViewRoot(RelativeLayout viewRoot) {
        this.viewRoot = viewRoot;
        /*
         * getChildAt(0) É O ImageButton QUE PERMITE A
         * MUDANÇA DE POSIÇÃO DO QUADRO COM WebView.
         * */
        this.viewRoot.getChildAt(0).setOnTouchListener( this );
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setParams() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ;
        }
        this.params = new WindowManager.LayoutParams(

                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            LAYOUT_FLAG =WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
        );
*/
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {

            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY ;
        }
        this.params = new WindowManager.LayoutParams(

                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
         WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/

            /*  //  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
        );*/



        this.params.gravity = Gravity.TOP | Gravity.START;
        this.params.x = 0;
        this.params.y = 150;
    }



    public  void closeservices(){
        button=viewRoot.findViewById(R.id.close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "hhhhh", Toast.LENGTH_SHORT).show();
            }
        });


    }













    public void updateViewRoot(String video ){
        WebView wv = ((WebView) viewRoot.findViewById(R.id.wv_video));
        ImageButton closeservice=viewRoot.findViewById(R.id.close);
        closeservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent  intent = new Intent(viewRoot.getContext(), VideoBubbleService.class);
                    viewRoot.getContext().stopService(intent);

            }
        });



        wv.getSettings().setJavaScriptEnabled( true );
        wv.setBackgroundColor(Color.TRANSPARENT);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.getSettings().setSupportMultipleWindows(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        //Intent intent=new Intent();
        wv.loadUrl( "http://www.youtube.com/embed/"+ video +"?autoplay=1&vq=small" );



        // PARA NÃO ABRIR O VÍDEO NO NAVEGADOR PADRÃO DO DEVICE.
        wv.setWebChromeClient( new WebChromeClient() );
        wv.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }
}
