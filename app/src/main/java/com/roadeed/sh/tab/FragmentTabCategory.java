package com.roadeed.sh.tab;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.fragments.FragmentCategory;
import com.roadeed.sh.fragments.FragmentRecent;
import com.roadeed.sh.utils.AppBarLayoutBehavior;
import com.roadeed.sh.utils.Constant;

public class FragmentTabCategory extends Fragment {
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private MainActivity mainActivity;
    public static Toolbar toolbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private View view;

    public static int single_tab = 1;
    public static int double_tab = 2;

    private int[] tabIcons = {
            R.drawable.ic_drawer_recent,
            R.drawable.ic_drawer_category
    };

    public FragmentTabCategory() {
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

        if (Config.ENABLE_TAB_LAYOUT) {
            view = inflater.inflate(R.layout.tab_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.tab_layout_fav, container, false);
        }

      //  Toast.makeText(getContext(), "category", Toast.LENGTH_SHORT).show();
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;
        setTaskBarColored(getActivity());
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
        viewPager.setOffscreenPageLimit(double_tab);
        toolbar = view.findViewById(R.id.toolbar);
      //  toolbar.setBackgroundColor(Constant.color);
        setupToolbar();

        appBarLayout.setBackgroundColor(Constant.color);
        //toolbar.inflateMenu(R.menu.menu_main);


        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(1);

        if (Config.ENABLE_TAB_LAYOUT) {
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                    tabLayout.setBackgroundColor(Constant.color);

                    //setupTabIcon();
                }
            });
        } else {
            tabLayout.setVisibility(View.GONE);
        }

        return view;

    }

    // public void setupTabIcon() {
    // tabLayout.getTabAt(0).setIcon(tabIcons[0]);
    // tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    // }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (Config.ENABLE_TAB_LAYOUT) {
                switch (position) {
                    case 0:
                        return new FragmentRecent();
                    case 1:
                        return new FragmentCategory();
                }
            } else {
                switch (position) {
                    case 0:
                        return new FragmentRecent();
                }
            }
            return null;
        }

        @Override
        public int getCount() {

            if (Config.ENABLE_TAB_LAYOUT) {
                return double_tab;
            } else {
                return single_tab;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (Config.ENABLE_TAB_LAYOUT) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.tab_recent);
                    case 1:
                        return getResources().getString(R.string.tab_category);
                }
            } else {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.tab_recent);
                }
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
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Intent intent = new Intent(getActivity(), SearchCategoryActivity.class);
               // startActivity(intent);
            }
        });
        if (Config.ENABLE_TAB_LAYOUT) {
            Log.d("Log", "Tab Layout is Enabled");
        } else {
            toolbar.setSubtitle(getString(R.string.tab_recent));
        }
            mainActivity.setSupportActionBar(toolbar);
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

