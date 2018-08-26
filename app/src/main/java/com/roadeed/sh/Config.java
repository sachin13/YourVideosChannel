package com.roadeed.sh;

import com.google.firebase.iid.FirebaseInstanceId;

public class Config {

    //put your admin panel url
    public static final String ADMIN_PANEL_URL = "http://sharq1.com/vidt";
    public static final String APP_NAME =  "روايد";
    public static final String LOG_PREFIX = "[" + APP_NAME + "]";

    //put your api key which obtained from admin panel
    // public static final String API_KEY = "cda11QbXIO9Z4ly0a2khTFDPA3x6UgSVtCiYRjBqpsfL7w5neN";
    public static final String API_KEY = "cda110cIwSefQziPgoTXu3hsREKWNVYOpAFJZCky9M4Br7bD5L";
    public static final String TOKEN = FirebaseInstanceId.getInstance().getToken();
    //Ads Configuration
    //set true to enable or set false to disable
    public static final boolean ENABLE_ADMOB_BANNER_ADS = true;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS = false;
    public static final int ADMOB_INTERSTITIAL_ADS_INTERVAL = 3;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO = false;

    //layout customization
    public static final boolean ENABLE_TAB_LAYOUT = true;
    public static boolean ENABLE_SINGLE_ROW_COLUMN = true;
    public static final boolean FORCE_PLAYER_TO_LANDSCAPE = false;
    public static final boolean ENABLE_VIEW_COUNT = true;
    public static final boolean ENABLE_DATE_DISPLAY = true;
    public static final boolean DISPLAY_DATE_AS_TIME_AGO = true;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = true;

    //load more for next list videos
    public static final int LOAD_MORE = 25;
    public static final int LOAD_MORE1 = 10000;


    //splash screen duration in millisecond
    public static final int SPLASH_TIME = 3000;

    //background sound
    public static final String YOUTUBE_TYPE = "YT_MEDIA_TYPE";
    public static final String YOUTUBE_TYPE_VIDEO = "YT_VIDEO";
    public static final String YOUTUBE_TYPE_PLAYLIST= "YT_PLAYLIST";
    public static final String YOUTUBE_TYPE_PLAYLIST_VIDEO_POS = "YT_PLAYLIST_VIDEO_POS";
    public static final String YOUTUBE_BASE_URL = "http://youtube.com/watch?v=";




    public static final int MAX_WIDTH_ICON = 128;  // pixels
    public static final int MAX_HEIGHT_ICON = 128;  // pixels



}