package com.roadeed.sh.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.roadeed.sh.BackgroundAudioService;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        System.out.println("Alarma Reciver Called");

        if (isMyServiceRunning(this.context, BackgroundAudioService.class)) {
            System.out.println("alredy running no need to start again");
        } else {
            Intent background = new Intent(context, BackgroundAudioService.class);
            context.startService(background);
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
