package com.roadeed.sh.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.activities.ActivityDetailVideo;
import com.roadeed.sh.activities.SearchtwoActivity;
import com.roadeed.sh.adapters.AdapterRecent;
import com.roadeed.sh.callbacks.CallbackListVideo;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.rests.ApiInterface;
import com.roadeed.sh.rests.RestAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.roadeed.sh.utils.Constant;
import com.roadeed.sh.utils.EqualSpacingItemDecoration;
import com.roadeed.sh.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FragmentRecent extends Fragment {

    View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterRecent adapterRecent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackListVideo> callbackCall = null;
    private int post_total = 0;
    private int failed_page = 0;
    private InterstitialAd interstitialAd;
    int counter = 1;
    int gridNumber=2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_recent, null);
        parent_view = getActivity().findViewById(R.id.main_content);

        loadInterstitialAd();

        setHasOptionsMenu(true);

        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue, R.color.colorRed);



        recyclerView = root_view.findViewById(R.id.recyclerView);

        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(0));
        // recycler display
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);
        if(!((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2)) == 0)) {
            gridNumber = sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridNumber));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterRecent = new AdapterRecent(getActivity(), recyclerView, new ArrayList<Video>(), gridNumber);
        recyclerView.setAdapter(adapterRecent);

        // on item list clicked
        adapterRecent.setOnItemClickListener(new AdapterRecent.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position, List<Video> list) {
                //Toast.makeText(getContext(), ""+obj.video_title, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ActivityDetailVideo.class);
                intent.putExtra(Constant.POSITION, position);
                intent.putExtra(Constant.LIST, (ArrayList) list);
                Log.d("VFUrl",list.get(position).getVideo_url());
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, obj.cat_id);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_NAME, obj.category_name);
                intent.putExtra(Constant.KEY_VID, obj.vid);
                intent.putExtra(Constant.KEY_VIDEO_TITLE, obj.video_title);
                intent.putExtra(Constant.KEY_VIDEO_URL, obj.video_url);
                //Toast.makeText(getContext(), ""+obj.video_url, Toast.LENGTH_SHORT).show();
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

        // detect when scroll reach bottom
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

        return root_view;
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
        callbackCall = apiInterface.getPostByPage(page_no, Config.LOAD_MORE);
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
        if (Tools.isConnect(getActivity())) {
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

    private void showFailedView(boolean show, String message) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed_home);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        root_view.findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.no_post_found);
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
            interstitialAd = new InterstitialAd(getActivity());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search:
                Intent intent = new Intent(getContext(), SearchtwoActivity.class);
                intent.putExtra(Constant.TYPE,Constant.KEY_VIDEO);
                startActivity(intent);
                return true;
            case R.id.savenotification:

                return true;

            case R.id.view:
                Fragment fragment = new SettingFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
