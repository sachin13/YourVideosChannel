package com.roadeed.sh.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roadeed.sh.R;
import com.roadeed.sh.activities.ActivityDetailVideo;
import com.roadeed.sh.adapters.AdapterFavorite;
import com.roadeed.sh.databases.DatabaseHandlerFavorite;
import com.roadeed.sh.models.Video;
import com.roadeed.sh.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentFavorite extends Fragment {

    private List<Video> data = new ArrayList<Video>();
    View root_view, parent_view;
    AdapterFavorite mAdapterFavorite;
    DatabaseHandlerFavorite databaseHandler;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    int gridNumber = 2;////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_favorite, null);
        parent_view = getActivity().findViewById(R.id.main_content);
        setHasOptionsMenu(true);

        linearLayout = root_view.findViewById(R.id.lyt_no_favorite);
        recyclerView = root_view.findViewById(R.id.recyclerView);

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());/////////////////////////////////
        //recyclerView.setLayoutManager(mLayoutManager);/////////////////////////////////////////////////////////////////////
        // recycler display//////////////////////////////////////////////////////////////////////
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);//////////
        if(!((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2)) == 0)) {///////////////////////
            gridNumber = sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridNumber));////////////////////////////////
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }///////////////////////////////////////////////////////////
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseHandler = new DatabaseHandlerFavorite(getActivity());
        data = databaseHandler.getAllData();

        mAdapterFavorite = new AdapterFavorite(getActivity(), recyclerView, data, gridNumber);
        recyclerView.setAdapter(mAdapterFavorite);

        if (data.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }

        return root_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();

        data = databaseHandler.getAllData();
        mAdapterFavorite = new AdapterFavorite(getActivity(), recyclerView, data, gridNumber);
        recyclerView.setAdapter(mAdapterFavorite);

        mAdapterFavorite.setOnItemClickListener(new AdapterFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Video obj, int position, List<Video> list) {
                Intent intent = new Intent(getActivity(), ActivityDetailVideo.class);
                intent.putExtra(Constant.POSITION, position);
                intent.putExtra(Constant.LIST, (ArrayList) list);
                Log.d("VFUrl",list.get(position).getVideo_url());
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, obj.cat_id);
                Log.d("catIdF", obj.cat_id);
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

        if (data.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
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
                if(mAdapterFavorite.getItemCount()>0) {
                    Intent intent = new Intent(getContext(), SearchFavoriteActivity.class);
                    intent.putExtra(Constant.TYPE,Constant.KEY_VIDEO);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "Your favorite list is empty", Toast.LENGTH_SHORT).show();
                }

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
