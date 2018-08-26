package com.roadeed.sh.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.WelcomeActivity;
import com.roadeed.sh.adapters.AdapterRecent;
import com.roadeed.sh.callbacks.CallbackListVideo;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.rests.ApiInterface;
import com.roadeed.sh.rests.RestAdapter;
import com.roadeed.sh.utils.Constant;
import com.roadeed.sh.utils.EqualSpacingItemDecoration;
import com.roadeed.sh.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.roadeed.sh.tab.FragmentTabCategory.toolbar;

public  class SearchtwoActivity extends AppCompatActivity  implements  SearchView.OnQueryTextListener{
    private RecyclerView recyclerView;
    private List<Video> VideotList;
    View root_view, parent_view;
    private AdapterRecent adapterRecent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackListVideo> callbackCall = null;
    private int post_total = 0;
    private int failed_page = 0;
    private InterstitialAd interstitialAd;
    int counter = 1;
    private SearchView searchView;
    Toolbar toolbar;
    String searchType;
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    int gridNumber = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchtwo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView = findViewById(R.id.etSearch);
        searchView.setVisibility(View.VISIBLE);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(SearchtwoActivity.this);
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
        toolbar.setBackgroundColor(Constant.color);
       // setTaskBarColored(SearchtwoActivity.this);
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        //searchView.setIconifiedByDefault(false);
        /*int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));*/
        //magImage.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(this);
        //getSupportActionBar().setTitle(R.string.search);

       /* Intent searchIntent = getIntent();
        if (searchIntent != null) {
            searchType = searchIntent.getStringExtra(Constant.TYPE);
        }*/
        parent_view = this.findViewById(R.id.main_content);
        loadInterstitialAd();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue, R.color.colorRed);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(0));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);//////
        if(!((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,1)) == 0)) {////////////////////////////
            gridNumber = sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2);
            recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        }////////////////////////////////////////////////////////////////////////////////////////////////
        //set data and list adapter
        adapterRecent = new AdapterRecent(this, recyclerView, new ArrayList<Video>(),gridNumber);
        recyclerView.setAdapter(adapterRecent);
        adapterRecent.setOnItemClickListener(new AdapterRecent.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position, List<Video> list) {

                Intent intent = new Intent(SearchtwoActivity.this, ActivityDetailVideo.class);
                intent.putExtra(Constant.POSITION, position);
                intent.putExtra(Constant.LIST, (ArrayList) list);
                Log.d("VFUrl",list.get(position).getVideo_url());
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, obj.cat_id);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_NAME, obj.category_name);
                intent.putExtra(Constant.KEY_VID, obj.vid);
                intent.putExtra(Constant.KEY_VIDEO_TITLE, obj.video_title);
                intent.putExtra(Constant.KEY_VIDEO_URL, obj.video_url);
                intent.putExtra(Constant.KEY_VIDEO_ID, obj.video_id);
                intent.putExtra(Constant.KEY_VIDEO_THUMBNAIL, obj.video_thumbnail);
                intent.putExtra(Constant.KEY_VIDEO_DURATION, obj.video_duration);
                intent.putExtra(Constant.KEY_VIDEO_DESCRIPTION, obj.video_description);
                intent.putExtra(Constant.KEY_VIDEO_TYPE, obj.video_type);
                intent.putExtra(Constant.KEY_TOTAL_VIEWS, obj.total_views);
                intent.putExtra(Constant.KEY_DATE_TIME, obj.date_time);
                startActivity(intent);

                showInterstitialAd();

            }
        });



        adapterRecent.setOnLoadMoreListener(new AdapterRecent.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > adapterRecent.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    adapterRecent.setLoaded();
                }
            }
        });

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                adapterRecent.resetListData();
                requestAction(1);
            }
        });

        requestAction(1);

        return ;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayApiResult(final List<Video> videos) {
        adapterRecent.insertData(videos);
        swipeProgress(false);
        if (videos.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestListPostApi(final int page_no) {
        ApiInterface apiInterface = RestAdapter.createAPI();
        callbackCall = apiInterface.getPostByPage(page_no, Config.LOAD_MORE1);
        callbackCall.enqueue(new Callback<CallbackListVideo>() {
            @Override
            public void onResponse(Call<CallbackListVideo> call, Response<CallbackListVideo> response) {
                CallbackListVideo resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.posts);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackListVideo> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterRecent.setLoaded();
        swipeProgress(false);
        if (Tools.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterRecent.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListPostApi(page_no);
            }
        }, Constant.DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       /* getMenuInflater().inflate(R.menu.searchmenu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(this);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            super.onBackPressed();
         }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        /*if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }*/
        super.onBackPressed();
    }


    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item_home);
        ((TextView) findViewById(R.id.no_item_message)).setText(R.string.no_post_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            return;
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            interstitialAd = new InterstitialAd(this);
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

                if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        } else {
            Log.d("AdMob", "AdMob Interstitial is Disabled");
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
       /* if(!TextUtils.isEmpty(searchType)){
            if(searchType.matches(Constant.KEY_VIDEO_CATEGORY_NAME)) {
                Toast.makeText(this, "cat", Toast.LENGTH_SHORT).show();
            }}else {*/
            List<Video> newList = new ArrayList<>();
            List<Video> newList2 = adapterRecent.updateList(newList, query);
      //  }
      //  adapterRecent = new AdapterRecent(this, recyclerView, newList2,query);
       // recyclerView.setAdapter(adapterRecent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        //String userInput = newText.toLowerCase();
       /* if(!TextUtils.isEmpty(searchType)){
        if(searchType.matches(Constant.KEY_VIDEO_CATEGORY_NAME)) {
            List<Video> newList = new ArrayList<>();
            List<Video> newList2 = adapterRecent.updateList(newList, query);
        }}else {*/
            List<Video> newList = new ArrayList<>();
            List<Video> newList2 = adapterRecent.updateList(newList, query);
      //  }
       // adapterRecent = new AdapterRecent(this, recyclerView, newList2,query);
       // recyclerView.setAdapter(adapterRecent);
       // adapterRecent.getFilter().filter(query);
        return false;
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
           // int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(Constant.color);
        }
    }



}