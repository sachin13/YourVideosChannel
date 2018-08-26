package com.roadeed.sh.tab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roadeed.sh.R;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.fragments.FragmentFavorite;
import com.roadeed.sh.fragments.SearchFavoriteActivity;
import com.roadeed.sh.utils.AppBarLayoutBehavior;
import com.roadeed.sh.utils.Constant;

public class FragmentTabFavorite extends Fragment {
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private MainActivity mainActivity;
    private Toolbar toolbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int single_tab = 1;

    public FragmentTabFavorite() {
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

        View view = inflater.inflate(R.layout.tab_layout_fav, container, false);
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
        AppBarLayout appBarLayout = view.findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);

        toolbar = view.findViewById(R.id.toolbar);
     //   toolbar.setBackgroundColor(Constant.color);
        appBarLayout.setBackgroundColor(Constant.color);
        setupToolbar();
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(1);
        tabLayout.setVisibility(View.GONE);

        return view;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentFavorite();
            }
            return null;
        }

        @Override
        public int getCount() {
            return single_tab;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.drawer_favorite);
            }
            return null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.drawer_favorite));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchFavoriteActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.setSupportActionBar(toolbar);
    }

}

