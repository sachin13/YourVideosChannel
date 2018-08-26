package com.roadeed.sh.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.cht.view.LoginActivity;
import com.roadeed.sh.fragments.FragmentAbout;
import com.roadeed.sh.fragments.FragmentNotification2;
import com.roadeed.sh.fragments.SettingFragment;
import com.roadeed.sh.tab.FragmentTabCategory;
import com.roadeed.sh.tab.FragmentTabFavorite;
import com.roadeed.sh.tab.FragmentTabRecent;
import com.roadeed.sh.utils.Constant;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    String str_category, str_vid, str_title, str_url, str_video_id, str_thumbnail, str_duration, str_description, str_type, str_date_time;


    String VersionUpdate;

    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String SELECTED_TAG = "selected_index";
    private static int selectedIndex;
    private final static int COLLAPSING_TOOLBAR = 0;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
  //  BroadcastReceiver broadcastReceiver;
    NavigationView navigationView;
    private long exitTime = 0;
    private AdView adView;
    String   refreshedToken;
    View view;
    //static Toolbar toolbar;


    private View animationTarget;
    Animation anim;
    Button buttonChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        setContentView(R.layout.activity_main);

        app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
      setTaskBarColored(MainActivity.this);
        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
           setTheme(appTheme);
        }


        subscribeToPushService();
        view = findViewById(android.R.id.content);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("khtwotoken", refreshedToken);
        buttonChat = (Button) findViewById(R.id.button2);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotat);
        buttonChat.setAnimation(anim);
        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);




        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        loadBannerAd();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        if (savedInstanceState != null) {
            navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }

        selectedIndex = COLLAPSING_TOOLBAR;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new FragmentTabRecent(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                .commit();
        navigationView.setItemIconTintList(null);


      /*  broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constant.REGISTRATION_COMPLETE)) {
                    // now subscribe to global topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Constant.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };*/
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

// recycler display
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);
        if(((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2)) == 0)) {
            SharedPreferences.Editor recyclerDisplayEditor = sharedPreferences.edit();
            recyclerDisplayEditor.putInt(Constant.RECYCLER_GRID_NUMBER,2);
            boolean isDone = recyclerDisplayEditor.commit();
            recyclerDisplayEditor.apply();
        }




    }
















    private void subscribeToPushService() {



        try {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            Log.d("AndroidBash", "Subscribed");

            String token = FirebaseInstanceId.getInstance().getToken();


            // Log and toast
            Log.d("AndroidBash", token);

        }catch (Exception e)
        {

        }



        //    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }







    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }


 /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

  /*   @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.search:
                Intent intent = new Intent(getApplicationContext(), SearchtwoActivity.class);
                startActivity(intent);
                return true;
            case R.id.savenotification:

                return true;


            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }*/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_notification2:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FragmentNotification2(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;



            case R.id.drawer_recent:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FragmentTabRecent(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_category:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FragmentTabCategory(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_favorite:
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentTabFavorite(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                        .commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.drawer_setting:
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingFragment(), COLLAPSING_TOOLBAR_FRAGMENT_TAG)
                        .commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.drawer_rate:

                final String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }

                return true;

            case R.id.drawer_more:

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));

                return true;

            case R.id.drawer_share:

                String app_name = android.text.Html.fromHtml(getResources().getString(R.string.app_name)).toString();
                String share_text = android.text.Html.fromHtml(getResources().getString(R.string.share_text)).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n\n" + getString(R.string.name1) + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;

            case R.id.drawer_about:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentAbout(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;



        }
        return false;
    }

    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
