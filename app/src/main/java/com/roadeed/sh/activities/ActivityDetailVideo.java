package com.roadeed.sh.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.roadeed.sh.BackgroundAudioService;
import com.roadeed.sh.Config;
import com.roadeed.sh.OreoBackgroundAudioService;
import com.roadeed.sh.R;
import com.roadeed.sh.VideoBubbleService;
import com.roadeed.sh.WelcomeActivity;
import com.roadeed.sh.databases.DatabaseHandlerFavorite;
import com.roadeed.sh.models.ItemType;
import com.roadeed.sh.models.Video;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.roadeed.sh.utils.Constant;
import com.roadeed.sh.utils.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ActivityDetailVideo extends AppCompatActivity {
    private static final String TAG = "ActivityDetailVideo";
    private static final int REQUEST_SYSTEM_ALERT_WINDOW = 500;
    public final static int REQUEST_CODE = 1234;
    String str_category, str_vid, str_title, str_url, str_video_id, str_thumbnail, str_duration, str_description, str_type, str_date_time;
    String show_video_title;
    String str_cid;
    long long_total_views;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    ImageView video_thumbnail;
    TextView txt_title, txt_category, txt_duration, txt_total_views, txt_date_time, txt_detail_controls_background, txt_detail_controls_popup;
    WebView video_description;
    Snackbar snackbar;
    private InterstitialAd interstitialAd;
    private AdView adView;
    ImageButton image_favorite;
    DatabaseHandlerFavorite databaseHandler;
    int position;
    List<Video> list;
    View view;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    ImageView mp4;
    Button btnDownload;
    String VideoPlayId;
    final Context context = this;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        view = findViewById(android.R.id.content);
        mp4 = (ImageView) findViewById(R.id.imageView4);
        txt_detail_controls_background = findViewById(R.id.detail_controls_background);
        txt_detail_controls_popup = findViewById(R.id.detail_controls_popup);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(ActivityDetailVideo.this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
       setTaskBarColored(ActivityDetailVideo.this);
        txt_detail_controls_popup.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkDrawOverlayPermission();

                }



            }
        });

        txt_detail_controls_background=findViewById(R.id.detail_controls_background);
        txt_detail_controls_background.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
           
         //  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            /*    new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG,"job running");
                        Log.d(TAG,"doback");


                        Log.d(TAG,"Job finished");
                        //doBackgroundWork();
                 /*  if(jobCancelled){
                    return;
                }


             try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                        //jobFinished(jobParameters,false);

             Intent serviceIntent = new Intent(ActivityDetailVideo.this, BackgroundAudioService.class);
               serviceIntent.setAction(BackgroundAudioService.ACTION_PLAY);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE, ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST, (ArrayList) list);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, position);
            startService(serviceIntent);
                  // }
                        //  }).start();
                /* ComponentName componentName = new ComponentName(ActivityDetailVideo.this, OreoBackgroundAudioService.class);
               JobInfo info = new JobInfo.Builder(123, componentName)
                       .setRequiresCharging(true)
                       .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                       .setPersisted(true)
                       .setPeriodic(15 * 60 * 1000)
                       .build();

               JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
               int resultCode = scheduler.schedule(info);
               if (resultCode == JobScheduler.RESULT_SUCCESS) {
                   Log.d(TAG, "Job scheduled");
               } else {
                   Log.d(TAG, "Job scheduling failed");
               }*/
          /* }else {
               Intent serviceIntent = new Intent(ActivityDetailVideo.this, BackgroundAudioService.class);
               serviceIntent.setAction(BackgroundAudioService.ACTION_PLAY);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE, ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST, (ArrayList) list);
               serviceIntent.putExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, position);
               startService(serviceIntent);
           }*/
            }
        });

        Picasso.with(context).load("http://sharq1.com/vd/mp4.png").into(mp4);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        loadBannerAd();
        loadInterstitialAd();

        databaseHandler = new DatabaseHandlerFavorite(getApplicationContext());

        Intent intent = getIntent();
        if (null != intent) {
            position = intent.getIntExtra(Constant.POSITION, 0);
            list = (ArrayList<Video>) intent.getSerializableExtra(Constant.LIST);
            Log.d("VDUrl",list.get(position).getVideo_url());
            str_cid = intent.getStringExtra(Constant.KEY_VIDEO_CATEGORY_ID);
            str_category = intent.getStringExtra(Constant.KEY_VIDEO_CATEGORY_NAME);
            str_vid = intent.getStringExtra(Constant.KEY_VID);
            str_title = intent.getStringExtra(Constant.KEY_VIDEO_TITLE);
            str_url = intent.getStringExtra(Constant.KEY_VIDEO_URL);
            //Toast.makeText(context, ""+str_url, Toast.LENGTH_SHORT).show();
            str_video_id = intent.getStringExtra(Constant.KEY_VIDEO_ID);
            str_thumbnail = intent.getStringExtra(Constant.KEY_VIDEO_THUMBNAIL);
            str_duration = intent.getStringExtra(Constant.KEY_VIDEO_DURATION);
            str_description = intent.getStringExtra(Constant.KEY_VIDEO_DESCRIPTION);
            str_type = intent.getStringExtra(Constant.KEY_VIDEO_TYPE);
            long_total_views = intent.getLongExtra(Constant.KEY_TOTAL_VIEWS, 0);
            str_date_time = intent.getStringExtra(Constant.KEY_DATE_TIME);


        }


        setupToolbar();

        video_thumbnail = findViewById(R.id.video_thumbnail);
        txt_title = findViewById(R.id.video_title);
        txt_category = findViewById(R.id.category_name);
        txt_duration = findViewById(R.id.video_duration);
        video_description = findViewById(R.id.video_description);
        txt_total_views = findViewById(R.id.total_views);
        txt_date_time = findViewById(R.id.date_time);
        image_favorite = findViewById(R.id.img_favorite);

        if (Config.ENABLE_RTL_MODE) {
            rtlLayout();
        } else {
            normalLayout();
        }

        addFavorite();

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Constant.color);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");

        }

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(str_category);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

    }

    public void normalLayout() {

        txt_title.setText(str_title);
        txt_category.setText(str_category);
        txt_duration.setText(str_duration);

        if (Config.ENABLE_VIEW_COUNT) {
            txt_total_views.setText(Tools.withSuffix(long_total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            txt_total_views.setVisibility(View.GONE);
        }

        if (Config.ENABLE_DATE_DISPLAY && Config.DISPLAY_DATE_AS_TIME_AGO) {
            PrettyTime prettyTime = new PrettyTime();
            long timeAgo = Tools.timeStringtoMilis(str_date_time);
            txt_date_time.setText(prettyTime.format(new Date(timeAgo)));
        } else if (Config.ENABLE_DATE_DISPLAY && !Config.DISPLAY_DATE_AS_TIME_AGO) {
            txt_date_time.setText(Tools.getFormatedDateSimple(str_date_time));
        } else {
            txt_date_time.setVisibility(View.GONE);
        }


        if (str_type != null && str_type.equals("youtube")) {
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMAGE_FRONT + str_video_id + Constant.YOUTUBE_IMAGE_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        } else {
            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + str_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        }

        txt_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityDetailVideo.this, ActivityRelatedCategory.class);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, str_cid);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_NAME, str_category);
                startActivity(intent);
            }
        });

        video_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Tools.isNetworkAvailable(ActivityDetailVideo.this)) {

                    if (str_type != null && str_type.equals("youtube")) {
                        Intent intent = new Intent(ActivityDetailVideo.this, ActivityYoutubePlayer.class);
                        intent.putExtra(Constant.KEY_VIDEO_ID, str_video_id);
                        startActivity(intent);
                    } else if (str_type != null && str_type.equals("Upload")) {
                        Intent intent = new Intent(ActivityDetailVideo.this, ActivityVideoPlayer.class);
                        intent.putExtra("url", Config.ADMIN_PANEL_URL + "/upload/video/" + str_url);
                        startActivity(intent);
                    } else {
                        if (str_url != null && str_url.startsWith("rtmp://")) {
                            Intent intent = new Intent(ActivityDetailVideo.this, ActivityRtmpPlayer.class);
                            intent.putExtra("url", str_url);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ActivityDetailVideo.this, ActivityVideoPlayer.class);
                            intent.putExtra("url", str_url);
                            startActivity(intent);
                        }
                    }

                    loadViewed();
                    showInterstitialAd();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_required), Toast.LENGTH_SHORT).show();
                }

            }
        });

        video_description.setBackgroundColor(Color.parseColor("#ffffff"));
        video_description.setFocusableInTouchMode(false);
        video_description.setFocusable(false);
        video_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = video_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = str_description;

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        video_description.loadData(text, mimeType, encoding);
    }

    public void rtlLayout() {

        txt_title.setText(str_title);
        txt_category.setText(str_category);
        txt_duration.setText(str_duration);

        if (Config.ENABLE_VIEW_COUNT) {
            txt_total_views.setText(Tools.withSuffix(long_total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            txt_total_views.setVisibility(View.GONE);
        }

        if (Config.ENABLE_DATE_DISPLAY && Config.DISPLAY_DATE_AS_TIME_AGO) {
            PrettyTime prettyTime = new PrettyTime();
            long timeAgo = Tools.timeStringtoMilis(str_date_time);
            txt_date_time.setText(prettyTime.format(new Date(timeAgo)));
        } else if (Config.ENABLE_DATE_DISPLAY && !Config.DISPLAY_DATE_AS_TIME_AGO) {
            txt_date_time.setText(Tools.getFormatedDateSimple(str_date_time));
        } else {
            txt_date_time.setVisibility(View.GONE);
        }


        if (str_type != null && str_type.equals("youtube")) {
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMAGE_FRONT + str_video_id + Constant.YOUTUBE_IMAGE_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        } else {
            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + str_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        }

        txt_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityDetailVideo.this, ActivityRelatedCategory.class);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, str_cid);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_NAME, str_category);
                startActivity(intent);
            }
        });

        video_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Tools.isNetworkAvailable(ActivityDetailVideo.this)) {

                    if (str_type != null && str_type.equals("youtube")) {
                        Intent intent = new Intent(ActivityDetailVideo.this, ActivityYoutubePlayer.class);
                        intent.putExtra(Constant.KEY_VIDEO_ID, str_video_id);
                        startActivity(intent);
                    } else if (str_type != null && str_type.equals("Upload")) {
                        Intent intent = new Intent(ActivityDetailVideo.this, ActivityVideoPlayer.class);
                        intent.putExtra("url", Config.ADMIN_PANEL_URL + "/upload/video/" + str_url);
                        startActivity(intent);
                    } else {
                        if (str_url != null && str_url.startsWith("rtmp://")) {
                            Intent intent = new Intent(ActivityDetailVideo.this, ActivityRtmpPlayer.class);
                            intent.putExtra("url", str_url);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ActivityDetailVideo.this, ActivityVideoPlayer.class);
                            intent.putExtra("url", str_url);
                            startActivity(intent);
                        }
                    }

                    loadViewed();
                    showInterstitialAd();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_required), Toast.LENGTH_SHORT).show();
                }

            }
        });


        video_description.setBackgroundColor(Color.parseColor("#ffffff"));
        video_description.setFocusableInTouchMode(false);
        video_description.setFocusable(false);
        video_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = video_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = str_description;

        String text = "<html dir='rtl'><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        video_description.loadData(text, mimeType, encoding);
    }

    private void loadViewed() {
        new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/get_total_views/?id=" + str_vid);
    }

    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Tools.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.share:

                String share_title = android.text.Html.fromHtml(str_title).toString();
                String share_content = android.text.Html.fromHtml(getResources().getString(R.string.share_text)).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + str_url + "\n\n" + getString(R.string.name) + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void addFavorite() {

        List<Video> data = databaseHandler.getFavRow(str_vid);
        if (data.size() == 0) {
            image_favorite.setImageResource(R.drawable.ic_fav_outline);
        } else {
            if (data.get(0).getVid().equals(str_vid)) {
                image_favorite.setImageResource(R.drawable.ic_fav);
            }
        }

        image_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Video> data = databaseHandler.getFavRow(str_vid);
                if (data.size() == 0) {
                    databaseHandler.AddtoFavorite(new Video(
                            str_cid,
                            str_category,
                            str_vid,
                            str_title,
                            str_url,
                            str_video_id,
                            str_thumbnail,
                            str_duration,
                            str_description,
                            str_type,
                            long_total_views,
                            str_date_time
                    ));
                    snackbar = Snackbar.make(view, getResources().getString(R.string.favorite_added), Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    image_favorite.setImageResource(R.drawable.ic_fav);

                } else {
                    if (data.get(0).getVid().equals(str_vid)) {
                        databaseHandler.RemoveFav(new Video(str_vid));
                        snackbar = Snackbar.make(view, getResources().getString(R.string.favorite_removed), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        image_favorite.setImageResource(R.drawable.ic_fav_outline);
                    }
                }
            }
        });


        mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_vid.equals("url")) {
                    downloadFromUrl(str_url, getDownloadTitle(getFileName(str_url)), getFileName(str_url));
                } else {
                    Intent i = new Intent(ActivityDetailVideo.this, ActivityYtDownload.class);
                    // Toast.makeText(context, ""+str_url, Toast.LENGTH_SHORT).show();
                    i.putExtra(Constant.KEY_VIDEO_URL, str_url);
                    i.putExtra("Name", str_title);
                    startActivity(i);
                }

            }
        });


    }


    public String getFileName(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    public String getDownloadTitle(String urlString) {
        int index = urlString.lastIndexOf('.');
        if (index == -1) {
            return urlString;
        } else {
            return urlString.substring(0, index);
        }
    }


    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        manager.enqueue(request);
    }


    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            adView.loadAd(new AdRequest.Builder().build());
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            Log.d("AdMob", "AdMob Banner is Disabled");
        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } else {
            Log.d("AdMob", "AdMob Interstitial is Disabled");
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        } else {
            Log.d("AdMob", "AdMob Interstitial is Disabled");
        }
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // You don't have permission
                    checkPermission();
                } else {
                    // Do as per your logic
                }
            }

        }
    }*/

    public void closeVideoFloatingWindow(View v) {
        encerrarVideoFloatingWindow();
    }

    private void encerrarVideoFloatingWindow() {
        stopService(new Intent(this, VideoBubbleService.class));
    }


    @SuppressLint("NewApi")
    public void checkDrawOverlayPermission() {
            if (!Settings.canDrawOverlays(ActivityDetailVideo.this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);

        }


        else
        {
           Intent intent = new Intent(ActivityDetailVideo.this, VideoBubbleService.class);
           intent.putExtra(Constant.KEY_VIDEO_ID, str_video_id);
          startService(intent);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(ActivityDetailVideo.this)) {
                   Intent intent = new Intent(ActivityDetailVideo.this, VideoBubbleService.class);
                  intent.putExtra(Constant.KEY_VIDEO_ID, str_video_id);
                  startService(intent);

                }
            }
        }
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













