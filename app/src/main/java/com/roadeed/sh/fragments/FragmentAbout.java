package com.roadeed.sh.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roadeed.sh.R;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.activities.SearchtwoActivity;
import com.roadeed.sh.adapters.AdapterAbout;
import com.roadeed.sh.utils.AppBarLayoutBehavior;
import com.roadeed.sh.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class FragmentAbout extends Fragment {
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    View root_view, parent_view;
    RecyclerView recyclerView;
    private Toolbar toolbar;
    AdapterAbout adapterAbout;
    private MainActivity mainActivity;

    public FragmentAbout() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_about, null);
        parent_view = getActivity().findViewById(R.id.main_content);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            mainActivity.setTheme(Constant.theme);
        }else if (appTheme == 0){
            mainActivity.setTheme(Constant.theme);
        }else{
            mainActivity.setTheme(appTheme);
        }
        AppBarLayout appBarLayout = root_view.findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        toolbar = root_view.findViewById(R.id.toolbar);
       // toolbar.setBackgroundColor(Constant.color);
        appBarLayout.setBackgroundColor(Constant.color);
        setupToolbar();

        recyclerView = root_view.findViewById(R.id.rvAllUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterAbout = new AdapterAbout(getDataInformation(), getActivity());
        recyclerView.setAdapter(adapterAbout);

        return root_view;
    }

    private List<Data> getDataInformation() {

        List<Data> data = new ArrayList<>();

        data.add(new Data(
                R.drawable.ic_other_appname,
                getResources().getString(R.string.about_app_name),
                getResources().getString(R.string.app_name)
        ));

        data.add(new Data(
                R.drawable.ic_other_build,
                getResources().getString(R.string.about_app_version),
                getResources().getString(R.string.sub_about_app_version)
        ));

        data.add(new Data(
                R.drawable.ic_other_email,
                getResources().getString(R.string.about_app_email),
                getResources().getString(R.string.sub_about_app_email)
        ));

        data.add(new Data(
                R.drawable.ic_other_copyright,
                getResources().getString(R.string.about_app_copyright),
                getResources().getString(R.string.sub_about_app_copyright)
        ));

        data.add(new Data(
                R.drawable.ic_other_rate,
                getResources().getString(R.string.about_app_rate),
                getResources().getString(R.string.sub_about_app_rate)
        ));

        data.add(new Data(
                R.drawable.ic_other_more,
                getResources().getString(R.string.about_app_more),
                getResources().getString(R.string.sub_about_app_more)
        ));

        data.add(new Data(
                R.drawable.ic_other_privacy,
                getResources().getString(R.string.about_app_privacy_policy),
                getResources().getString(R.string.sub_about_app_privacy_policy)
        ));

        return data;
    }

    public class Data {
        private int image;
        private String title;
        private String sub_title;

        public int getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getSub_title() {
            return sub_title;
        }

        public Data(int image, String title, String sub_title) {
            this.image = image;
            this.title = title;
            this.sub_title = sub_title;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.drawer_about));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchtwoActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.setSupportActionBar(toolbar);
    }

}