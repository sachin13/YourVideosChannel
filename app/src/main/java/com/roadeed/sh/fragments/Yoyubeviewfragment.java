package com.roadeed.sh.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.roadeed.sh.R;

public class Yoyubeviewfragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    private static final String API_KEY = "";
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener myPlaybackEventListener;

    private String videoId = "qNggvk8QtaY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_youtube, container, false);
        playerStateChangeListener = new MyPlayerStateChangeListener();
        myPlaybackEventListener = new MyPlaybackEventListener();

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtube_view);
        youTubePlayerView.initialize(API_KEY, this);

        return view;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(getActivity(), "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(myPlaybackEventListener);

        if (!wasRestored) {
            player.cueVideo(videoId);
        }
    }
    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            //showInterstitialAd();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        private void updateLog(String prompt){
         /*   log +=  "MyPlaybackEventListener" + "\n-" +
                    prompt + "\n\n=====";
            textVideoLog.setText(log);*/
        };

        @Override
        public void onBuffering(boolean arg0) {
            updateLog("onBuffering(): " + String.valueOf(arg0));
        }

        @Override
        public void onPaused() {
            updateLog("onPaused()");
        }

        @Override
        public void onPlaying() {
            updateLog("onPlaying()");
        }

        @Override
        public void onSeekTo(int arg0) {
            updateLog("onSeekTo(): " + String.valueOf(arg0));
        }

        @Override
        public void onStopped() {
            updateLog("onStopped()");
        }

    }
}
