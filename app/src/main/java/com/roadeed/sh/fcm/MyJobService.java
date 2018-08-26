package com.roadeed.sh.fcm;

import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}
/*
public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    private NotificationUtils notificationUtils;


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.

        new Thread(new Runnable() {
            @Override
            public void run() {
               codeYouWantToRun(jobParameters);
            }
        }).start();

        return false;
    }

    private void codeYouWantToRun(JobParameters jobParameters) {
        try {

            Log.d(TAG, "completeJob: " + "jobStarted");
            Log.d(TAG, "JobParams: " + jobParameters.getExtras().toString());
            //This task takes 2 seconds to complete.
            try {
                JSONObject json = new JSONObject(jobParameters.getExtras().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            Thread.sleep(15000);

            Log.d(TAG, "completeJob: " + "jobFinished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //Tell the framework that the job has completed and doesnot needs to be reschedule
            jobFinished(jobParameters, true);
        }
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");
            Log.d( "notofication: " , data.toString()+"");
            String title = data.getString("title");
            String message = data.getString("title");
            String imageUrl = data.getString("image");


            Log.e("dataaa", "title: " + title);
            Log.e("dataaa", "message: " + message);
            Log.e("dataaa", "imageUrl: " + imageUrl);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    displayNotificationMessage(getApplicationContext(), title, message, resultIntent);
                } else {
                    // image is present, show notification with image
                    displayNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void displayNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    private void displayNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }
}
*/