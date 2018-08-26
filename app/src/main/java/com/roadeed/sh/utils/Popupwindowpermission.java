package com.roadeed.sh.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Popupwindowpermission {
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays( context );
    }

    public static boolean isPlusEqualsApi13() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }
}
