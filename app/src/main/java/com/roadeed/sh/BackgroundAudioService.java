package com.roadeed.sh;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.roadeed.sh.activities.LogHelper;
import com.roadeed.sh.activities.MainActivity;
import com.roadeed.sh.activities.MyApplication;
import com.roadeed.sh.models.ItemType;
import com.roadeed.sh.models.Video;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

import sh.sharq.youtsharq.VideoMeta;
import sh.sharq.youtsharq.YouTubeExtractor;
import sh.sharq.youtsharq.YtFile;

//import com.bumptech.glide.request.transition.Transition;

/**
 * Service class for background youtube playback
 * Created by Stevan Medic on 9.3.16..
 */
public class BackgroundAudioService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private static final String TAG = "SMEDIC service";

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    private static final String NOTIFICATION_CHANNEL = "4565";
    private static YouTubeExtractor youTubeExtractor;
    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mSession;
    private MediaControllerCompat mController;

    private ItemType mediaType = ItemType.YOUTUBE_MEDIA_NONE;

    private Video videoItem;

    private boolean isStarting = false;
    Boolean onGoing = true ;
    private int currentSongIndex = 0;

    private ArrayList<Video> youTubeVideos;

    private NotificationCompat.Builder builder = null;

    private DeviceBandwidthSampler deviceBandwidthSampler;
    private ConnectionQuality connectionQuality = ConnectionQuality.MODERATE;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*if (/* Check if data needs to be processed by long running job *//* true) {
            // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            //scheduleJob();
            videoItem = new Video();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            //initMediaSessions();
            //initPhoneCallListener();
            deviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
            new OreoBackgroundAudioService("roadeed",deviceBandwidthSampler);
       } else {
            // Handle message within 10 seconds
            handleNow();
        }*/


        videoItem = new Video();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        initMediaSessions();
        initPhoneCallListener();
        deviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
    }

  /*  private void handleNow() {
        videoItem = new Video();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        initMediaSessions();
        initPhoneCallListener();
        deviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
    }*/


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_STICKY;
    }

    private void initPhoneCallListener() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    pauseVideo();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    Log.d(TAG, "onCallStateChanged: ");
                    resumeVideo();
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public void onDestroy() {
        stopPlayer();
        //remove notification and stop service
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        Intent intent = new Intent(getApplicationContext(), BackgroundAudioService.class);
        stopService(intent);
        super.onDestroy();
    }

    /**
     * Handles intent (player options play/pause/stop...)
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;
        String action = intent.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            handleMedia(intent);
            mController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            mController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            mController.getTransportControls().stop();
        }
    }

    /**
     * Handles media - playlists and videos sent from fragments
     *
     * @param intent
     */
    private void handleMedia(Intent intent) {
        ItemType intentMediaType = ItemType.YOUTUBE_MEDIA_NONE;
        if (intent.getSerializableExtra(Config.YOUTUBE_TYPE) != null) {
            intentMediaType = (ItemType) intent.getSerializableExtra(Config.YOUTUBE_TYPE);
        }
        switch (intentMediaType) {
            case YOUTUBE_MEDIA_NONE: //video is paused,so no new playback requests should be processed
                mMediaPlayer.start();
                break;
            case YOUTUBE_MEDIA_TYPE_VIDEO:
                mediaType = ItemType.YOUTUBE_MEDIA_TYPE_VIDEO;
                videoItem = (Video) intent.getSerializableExtra(Config.YOUTUBE_TYPE_VIDEO);
                if (videoItem.getVid()!= null) {
                    // Toast.makeText(this, ""+videoItem.getVideo_title(), Toast.LENGTH_SHORT).show();
                    playVideo();
                }
                break;
            case YOUTUBE_MEDIA_TYPE_PLAYLIST: //new playlist playback request
                mediaType = ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST;
                youTubeVideos = (ArrayList<Video>) intent.getSerializableExtra(Config.YOUTUBE_TYPE_PLAYLIST);
                int startPosition = intent.getIntExtra(Config.YOUTUBE_TYPE_PLAYLIST_VIDEO_POS, 0);
                videoItem = youTubeVideos.get(startPosition);
                currentSongIndex = startPosition;
                playVideo();
                break;
            default:
                Log.d(TAG, "Unknown command");
                break;
        }
    }

    /**
     * Initializes media sessions and receives media events
     */
    private void initMediaSessions() {
        // Make sure the media player will acquire a wake-lock while playing. If we don't do
        // that, the CPU might go to sleep while the song is playing, causing playback to stop.
        //
        // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
        // permission in AndroidManifest.xml.
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        PendingIntent buttonReceiverIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(Intent.ACTION_MEDIA_BUTTON),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mSession = new MediaSessionCompat(getApplicationContext(), "simple player session",
                null, buttonReceiverIntent);

        try {
            mController = new MediaControllerCompat(getApplicationContext(), mSession.getSessionToken());

            mSession.setCallback(
                    new MediaSessionCompat.Callback() {
                        @Override
                        public void onPlay() {
                            super.onPlay();
                            onGoing = true;
                            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                        }

                        @Override
                        public void onPause() {
                            super.onPause();
                            onGoing = false;
                            pauseVideo();
                            buildNotificationstop(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                        }

                        @Override
                        public void onSkipToNext() {
                            super.onSkipToNext();
                            if (!isStarting) {
                                playNext();
                            }
                            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                        }

                        @Override
                        public void onSkipToPrevious() {
                            super.onSkipToPrevious();
                            if (!isStarting) {
                                playPrevious();
                            }
                            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                        }

                        @Override
                        public void onStop() {
                            super.onStop();
                            stopPlayer();
                            //remove notification and stop service
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);
                            Intent intent = new Intent(getApplicationContext(), BackgroundAudioService.class);
                            stopService(intent);
                        }

                        @Override
                        public void onSetRating(RatingCompat rating) {
                            super.onSetRating(rating);
                        }
                    }
            );
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    /**
     * Field which handles image loading
     */
    private Target target = new Target()
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
        {
            updateNotificationLargeIcon(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable drawable)
        {
            LogHelper.d(TAG, "Load bitmap... failed");
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {}
    };


    /**
     * Builds notification panel with buttons and info on it
     *
     * @param action Action to be applied
     */

    private void buildNotification(NotificationCompat.Action action) {

        final MediaStyle style = new MediaStyle();

        Intent intent = new Intent(getApplicationContext(), BackgroundAudioService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.setAction(Intent.ACTION_MAIN);
        clickIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(videoItem.getVideo_title());
        builder.setContentInfo(videoItem.getVideo_duration());
        builder.setUsesChronometer(true);

        builder.setShowWhen(false);
        builder.setContentIntent(clickPendingIntent);
        builder.setDeleteIntent(stopPendingIntent);
        //if (onGoing) { builder.setOngoing(true);}else{builder.setOngoing(false);}
        builder.setOngoing(mMediaPlayer.isPlaying());
        //   builder.setAutoCancel(false);
        builder.setChannelId(NOTIFICATION_CHANNEL);
        builder.setSubText(videoItem.getSize());

        builder.setStyle(style);

        if (videoItem.getVideo_thumbnail() != null && !videoItem.getVideo_thumbnail().isEmpty()) {
            Picasso.with(this)
                    .load(videoItem.getVideo_thumbnail())
                    .resize(Config.MAX_WIDTH_ICON, Config.MAX_HEIGHT_ICON)
                    .centerCrop()
                    .into(target);
        }

        //load bitmap for largeScreen
        /*if (videoItem.getVideo_thumbnail() != null && !videoItem.getVideo_thumbnail().isEmpty()) {
            Glide.with(getApplicationContext()).asBitmap().load(videoItem.getVideo_thumbnail()).into(target);
        }*/

        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        //Notification notification = builder.build();
        if(onGoing) {
            startForeground(1, builder.build());
            builder.setAutoCancel(true);
            builder.setOngoing(false);

        }else {
            stopForeground(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager mNotificationManager2 =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String description = getApplicationContext().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL, getApplicationContext().getString(R.string.app_name), importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager2.createNotificationChannel(mChannel);
            //  Toast.makeText(this, "My app "+getApplicationContext().getString(R.string.app_name), Toast.LENGTH_LONG).show();


        }
    }

    private void buildNotificationstop(NotificationCompat.Action action) {

        final MediaStyle style = new MediaStyle();

        Intent intent = new Intent(getApplicationContext(), BackgroundAudioService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.setAction(Intent.ACTION_MAIN);
        clickIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(videoItem.getVideo_title());
        builder.setContentInfo(videoItem.getVideo_duration());
        builder.setShowWhen(false);
        builder.setContentIntent(clickPendingIntent);
        builder.setDeleteIntent(stopPendingIntent);
        builder.setChannelId(NOTIFICATION_CHANNEL);
        builder.setSubText(videoItem.getSize());
        builder.setStyle(style);
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        if(onGoing) {
            startForeground(1, builder.build());

        }else {
            stopForeground(false);
        }
        //load bitmap for largeScreen
       /* if (videoItem.getVideo_thumbnail() != null && !videoItem.getVideo_thumbnail().isEmpty()) {
            Glide.with(getApplicationContext()).asBitmap().load(videoItem.getVideo_thumbnail()).into(target);
        }*/

        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager mNotificationManager2 =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String description = getApplicationContext().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL, getApplicationContext().getString(R.string.app_name), importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            builder.setAutoCancel(true);
            builder.setOngoing(false);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager2.createNotificationChannel(mChannel);
            //  Toast.makeText(this, "My app "+getApplicationContext().getString(R.string.app_name), Toast.LENGTH_LONG).show();


        }
    }







    /**
     * Field which handles image loading

     private Target<Bitmap> target = new SimpleTarget<Bitmap>() {
    @Override
    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition transition) {
    updateNotificationLargeIcon(bitmap);
    }
    };
     */
    /**
     * Updates only large icon in notification panel when bitmap is decoded
     *
     * @param bitmap
     */
    private void updateNotificationLargeIcon(Bitmap bitmap) {
        builder.setLargeIcon(bitmap);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    /**
     * Generates specific action with parameters below
     *
     * @param icon
     * @param title
     * @param intentAction
     * @return
     */
    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), BackgroundAudioService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    /**
     * Plays next video in playlist
     */
    private void playNext() {
        //if media type is video not playlist, just loop it
        if (mediaType == ItemType.YOUTUBE_MEDIA_TYPE_VIDEO) {
            seekVideo(0);
            restartVideo();
            return;
        }

        if (youTubeVideos.size() > currentSongIndex + 1) {
            currentSongIndex++;
        } else { //play 1st song
            currentSongIndex = 0;
        }

        videoItem = youTubeVideos.get(currentSongIndex);
        playVideo();
    }

    /**
     * Plays previous video in playlist
     */
    private void playPrevious() {
        //if media type is video not playlist, just loop it
        if (mediaType == ItemType.YOUTUBE_MEDIA_TYPE_VIDEO) {
            restartVideo();
            return;
        }

        if (currentSongIndex - 1 >= 0) {
            currentSongIndex--;
        } else { //play last song
            currentSongIndex = youTubeVideos.size() - 1;
        }
        videoItem = youTubeVideos.get(currentSongIndex);
        playVideo();
    }

    /**
     * Plays video
     */
    private void playVideo() {
        isStarting = true;
        onGoing = true;
        extractUrlAndPlay();
    }

    /**
     * Pauses video
     */
    private void pauseVideo() {
        if (mMediaPlayer.isPlaying()) {
            onGoing = false;
            mMediaPlayer.pause();
        }
    }

    /**
     * Resumes video
     */
    private void resumeVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    /**
     * Restarts video
     */
    private void restartVideo() {
        mMediaPlayer.start();
    }

    /**
     * Seeks to specific time
     *
     * @param seekTo
     */
    private void seekVideo(int seekTo) {
        mMediaPlayer.seekTo(seekTo);
    }

    /**
     * Stops video
     */
    private void stopPlayer() {
        if(mMediaPlayer != null) {
            builder.setOngoing(false);
            onGoing = false;
            //stopForeground(true);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * Get the best available audio stream
     * <p>
     * Itags:
     * 141 - mp4a - stereo, 44.1 KHz 256 Kbps
     * 251 - webm - stereo, 48 KHz 160 Kbps
     * 140 - mp4a - stereo, 44.1 KHz 128 Kbps
     * 17 - mp4 - stereo, 44.1 KHz 96-100 Kbps
     *
     * @param ytFiles Array of available streams
     * @return Audio stream with highest bitrate
     */
    private YtFile getBestStream(SparseArray<YtFile> ytFiles) {

        connectionQuality = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
        int[] itags = new int[]{251, 141, 140, 17};

        if (connectionQuality != null && connectionQuality != ConnectionQuality.UNKNOWN) {
            switch (connectionQuality) {
                case POOR:
                    itags = new int[]{17, 140, 251, 141};
                    break;
                case MODERATE:
                    itags = new int[]{251, 141, 140, 17};
                    break;
                case GOOD:
                case EXCELLENT:
                    itags = new int[]{141, 251, 140, 17};
                    break;
            }
        }

        if (ytFiles.get(itags[0]) != null) {
            return ytFiles.get(itags[0]);
        } else if (ytFiles.get(itags[1]) != null) {
            return ytFiles.get(itags[1]);
        } else if (ytFiles.get(itags[2]) != null) {
            return ytFiles.get(itags[2]);
        }
        return ytFiles.get(itags[3]);
    }

    /**
     * Extracts link from youtube video ID, so mediaPlayer can play it
     */
    private void extractUrlAndPlay() {
        // String youtubeLink = Config.YOUTUBE_BASE_URL + videoItem.getId();
        String youtubeLink = videoItem.getVideo_url();
        //   Toast.makeText(this, ""+youtubeLink, Toast.LENGTH_SHORT).show();
        deviceBandwidthSampler.startSampling();

        youTubeExtractor = new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    Toast.makeText(MyApplication.getAppContext(), R.string.failed_playback,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                deviceBandwidthSampler.stopSampling();
                YtFile ytFile = getBestStream(ytFiles);
                try {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(ytFile.getUrl());
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();

                        Toast.makeText(MyApplication.getAppContext(), videoItem.getVideo_title(), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        };

        youTubeExtractor.execute(youtubeLink);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
    }

    @Override
    public void onCompletion(MediaPlayer _mediaPlayer) {
        if (mediaType == ItemType.YOUTUBE_MEDIA_TYPE_PLAYLIST) {
            playNext();
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        } else {
            restartVideo();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isStarting = false;
    }

   /* private void scheduleJob() {
        // [START dispatch_job]

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(OreoBackgroundAudioService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);

        //
    }*/

}