package com.roadeed.sh.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.roadeed.sh.Config;
import com.roadeed.sh.R;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.models.ColorChooserDialog1;
import com.roadeed.sh.models.Methods;
import com.roadeed.sh.utils.Constant;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import static android.content.Context.MODE_PRIVATE;


public class SettingFragment extends Fragment {
    private View root_view, parent_view;
    private MainActivity mainActivity;
    private Toolbar toolbar;
    TextView view;
    SharedPreferences sharedPreferences1, app_preferences;
    SharedPreferences.Editor editor;
    Button button;
    Methods methods;
    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root_view = inflater.inflate(R.layout.fragment_setting,null);
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
        //((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());
        toolbar = root_view.findViewById(R.id.toolbar);
        toolbar.setTitle("الضبظ");
       // toolbar.setBackgroundColor(Constant.color);
        appBarLayout.setBackgroundColor(Constant.color);
        methods = new Methods();
        button = root_view.findViewById(R.id.button_color);
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences1.edit();
        setupToolbar();
        colorize();
        methods.setColorTheme();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ColorChooserDialog dialog = new ColorChooserDialog(getActivity());
                dialog.setTitle("Select");
                
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        colorize();
                        Constant.color = color;
                        methods.setColorTheme();
                        editor.putInt("color", color);
                        editor.putInt("theme",Constant.theme);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
        view = root_view.findViewById(R.id.view);

        // This will get the radiogroup
        final RadioGroup rGroup = root_view.findViewById(R.id.radioGroup);
        // This will get the radiobutton in the radiogroup that is checked
        RadioButton checkedRadioButton = rGroup.findViewById(rGroup.getCheckedRadioButtonId());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rGroup.setVisibility(View.VISIBLE);
            }
        });
        // recycler display
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);
        if(!((sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2)) == 0)) {
            int gridNumber = sharedPreferences.getInt(Constant.RECYCLER_GRID_NUMBER,2);
            switch (gridNumber){
                case 1:
                    checkedRadioButton = rGroup.findViewById(R.id.onevideo);
                    break;
                case 2:
                    checkedRadioButton = rGroup.findViewById(R.id.twovideo);
                    break;
                case 3:
                    checkedRadioButton = rGroup.findViewById(R.id.threevideo);
                    break;
            }
        } else {
            checkedRadioButton = rGroup.findViewById(R.id.twovideo);
        }
        checkedRadioButton.setChecked(true);
        // This overrides the radiogroup onCheckListener
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            int grideNumber = 2;

            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                   // Toast.makeText(getActivity(), ""+checkedId, Toast.LENGTH_SHORT).show();
                    switch (checkedId){
                        case R.id.onevideo:
                            grideNumber = 1;
                            Config.ENABLE_SINGLE_ROW_COLUMN = true;//****************
                            break;
                        case R.id.twovideo:
                            grideNumber = 2;
                            Config.ENABLE_SINGLE_ROW_COLUMN = false;//****************
                            break;
                        case R.id.threevideo:
                            grideNumber = 3;
                            Config.ENABLE_SINGLE_ROW_COLUMN = false;//****************
                            break;
                    }
                            // recycler display
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.RECYCLER_DISPLAY,MODE_PRIVATE);
                    SharedPreferences.Editor recyclerDisplayEditor = sharedPreferences.edit();
                    recyclerDisplayEditor.putInt(Constant.RECYCLER_GRID_NUMBER, grideNumber);
                    boolean isDone = recyclerDisplayEditor.commit();
                    recyclerDisplayEditor.apply();
                    if(isDone){
                        Toast.makeText(getActivity(), "تم التعديل بنجاح" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }
        });
        return root_view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void colorize(){
        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setBounds(58, 58, 58, 58);
        d.getPaint().setStyle(Paint.Style.FILL);
        d.getPaint().setColor(Constant.color);
        button.setBackground(d);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }


    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.drawer_settings));
        // toolbar.setSubtitle(getString(R.string.drawer_about));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(getActivity(), SearchtwoActivity.class);
                //startActivity(intent);
            }
        });
        mainActivity.setSupportActionBar(toolbar);
    }
}