package com.roadeed.sh.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.roadeed.sh.R;
import com.roadeed.sh.activities.ActivityDetailVideo;
import com.roadeed.sh.activities.SearchtwoActivity;
import com.roadeed.sh.adapters.AdapterFavorite;
import com.roadeed.sh.databases.DatabaseHandlerFavorite;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class SearchFavoriteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private List<Video> data = new ArrayList<Video>();
    AdapterFavorite mAdapterFavorite;
    DatabaseHandlerFavorite databaseHandler;
    RecyclerView recyclerView;
    //LinearLayout linearLayout;
    private SearchView searchView;
    Toolbar toolbar;
    int gridNumber = 2;
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_favorite);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView = findViewById(R.id.etSearch);
        searchView.setVisibility(View.VISIBLE);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(SearchFavoriteActivity.this);
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
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        //searchView.setIconifiedByDefault(false);
        /*int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));*/
        //magImage.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(this);


        // linearLayout = findViewById(R.id.lyt_no_favorite);
        recyclerView = findViewById(R.id.recyclerView);

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);/////////////////////////
        //recyclerView.setLayoutManager(mLayoutManager);///////////////////////////////////////////////////////
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseHandler = new DatabaseHandlerFavorite(this);
        data = databaseHandler.getAllData();
        // recycler display////////////////////////////////////////////////////////////////////////////////////////////
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);//////
        if(!((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2)) == 0)) {////////////////////////////
            gridNumber = sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2);
            recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        }////////////////////////////////////////////////////////////////////////////////////////////////
        mAdapterFavorite = new AdapterFavorite(this, recyclerView, data, gridNumber);///////////////////////
        recyclerView.setAdapter(mAdapterFavorite);

        /*if (data.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }*/


    }


    @Override
    public void onResume() {

        super.onResume();

        data = databaseHandler.getAllData();
        mAdapterFavorite = new AdapterFavorite(this, recyclerView, data, gridNumber);////////////////////////////////
        recyclerView.setAdapter(mAdapterFavorite);

        mAdapterFavorite.setOnItemClickListener(new AdapterFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position,  List<Video> list) {
                Intent intent = new Intent(SearchFavoriteActivity.this, ActivityDetailVideo.class);
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
            }
        });

       /* if (data.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Video> newList = new ArrayList<>();
        List<Video> newList2 = mAdapterFavorite.updateList(newList, query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Video> newList = new ArrayList<>();
        List<Video> newList2 = mAdapterFavorite.updateList(newList, newText);
        return false;
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


}
