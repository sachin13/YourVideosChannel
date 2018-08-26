package com.roadeed.sh;


import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.roadeed.sh.activities.MyApplication;
import com.roadeed.sh.models.ItemType;
import com.roadeed.sh.models.Video;
import android.os.Process;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import sh.sharq.youtsharq.VideoMeta;
import sh.sharq.youtsharq.YouTubeExtractor;
import sh.sharq.youtsharq.YtFile;

public class OreoBackgroundAudioService extends IntentService implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {


    private static final String TAG = "roadeedJobService";
    private boolean jobCancelled = false;

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
    private int currentSongIndex = 0;

    private ArrayList<Video> youTubeVideos;

    private NotificationCompat.Builder builder = null;

    private DeviceBandwidthSampler deviceBandwidthSampler;
    private ConnectionQuality connectionQuality = ConnectionQuality.MODERATE;

    public OreoBackgroundAudioService(String name, DeviceBandwidthSampler deviceBandwidthSampler) {

        super(name);
        this.deviceBandwidthSampler = deviceBandwidthSampler;
    }

    public OreoBackgroundAudioService() {
        super("");
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
          /*  try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
          /*  stopSelf(msg.arg1);*/
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
*/
                Log.d(TAG,"job running");
                Log.d(TAG,"doback");

                videoItem = new Video();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(OreoBackgroundAudioService.this);
                mMediaPlayer.setOnPreparedListener(OreoBackgroundAudioService.this);
                initMediaSessions();
                initPhoneCallListener();
                deviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
                Log.d(TAG,"Job finished");
                //doBackgroundWork();
                 /*  if(jobCancelled){
                    return;
                }


             try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //jobFinished(jobParameters,false);
            }
        }).start();*/
    }


    /* @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG,"Job Started");
        doBackgroundWork(jobParameters);
        return true;
    }*/

  //  private void doBackgroundWork(final JobParameters jobParameters) {
        private void doBackgroundWork() {
            Log.d(TAG,"doback");

                videoItem = new Video();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(OreoBackgroundAudioService.this);
                mMediaPlayer.setOnPreparedListener(OreoBackgroundAudioService.this);
                initMediaSessions();
                initPhoneCallListener();
                deviceBandwidthSampler = DeviceBandwidthSampler.getInstance();


    }

   /* @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG,"Job Cancelled");
        jobCancelled = true;
        return false;
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    private void initPhoneCallListener() {
        Log.d(TAG,"call");
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
        Intent intent = new Intent(getApplicationContext(), OreoBackgroundAudioService.class);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
        Log.d(TAG,"session");
        // Make sure the media player will acquire a wake-lock while playing. If we don't do
        // that, the CPU might go to sleep while the song is playing, causing playback to stop.
        //
        // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
        // permission in AndroidManifest.xml.
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        final PendingIntent buttonReceiverIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                new Intent(Intent.ACTION_MEDIA_BUTTON),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        /*i Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

               f(msg.arg1==1)
                              {
                    //Print Toast or open dialog
                }
                return false;
            }
        });*/
        mSession = new MediaSessionCompat(getApplicationContext(), "simple player session",
                null, buttonReceiverIntent);
        try {
            mController = new MediaControllerCompat(getApplicationContext(), mSession.getSessionToken());

            mSession.setCallback(
                    new MediaSessionCompat.Callback() {
                        @Override
                        public void onPlay() {
                            super.onPlay();
                            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                        }

                        @Override
                        public void onPause() {

                            super.onPause();
                            pauseVideo();
                            buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
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
                            Intent intent = new Intent(getApplicationContext(), OreoBackgroundAudioService.class);
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
     * Builds notification panel with buttons and info on it
     *
     * @param action Action to be applied
     */

    private void buildNotification(NotificationCompat.Action action) {

        final android.support.v4.media.app.NotificationCompat.MediaStyle style = new android.support.v4.media.app.NotificationCompat.MediaStyle();

        Intent intent = new Intent(getApplicationContext(), OreoBackgroundAudioService.class);
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
        builder.setOngoing(false);
        builder.setChannelId(NOTIFICATION_CHANNEL);
        builder.setSubText(videoItem.getSize());
        builder.setStyle(style);

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
            mChannel.setLightColor(Color.RED);
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
        Intent intent = new Intent(getApplicationContext(), OreoBackgroundAudioService.class);
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
        extractUrlAndPlay();
    }

    /**
     * Pauses video
     */
    private void pauseVideo() {
        if (mMediaPlayer.isPlaying()) {
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
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
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
}
