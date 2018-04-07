package com.example.android.bakingrecipes.UI;

import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingrecipes.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**This video fragment will be used in the master detail flow layout for tablets in landscape mode.
 * The video will display the recipe step and will be replaced by a new one when the user clicks on a
 * different step.
 * Created by Silky on 20/03/2018.
 */

public class VideoFragment extends Fragment implements ExoPlayer.EventListener {

    private String videoURL;
    private SimpleExoPlayerView videoView;
    private SimpleExoPlayer player;
    private Long videoPosition = null;
    private Boolean getPlayWhenReady = true;
    private String PLAY_STATE = "Play_State";
    public static final String VIDEO_URL = "video_url";
    private static final String POSITION = "position";


    public VideoFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.video_fragment_layout, container, false);


        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(VIDEO_URL))
            videoURL = savedInstanceState.getString(VIDEO_URL);
            if(savedInstanceState.containsKey(POSITION))
                videoPosition = savedInstanceState.getLong(POSITION);
            if(savedInstanceState.containsKey(PLAY_STATE))
                getPlayWhenReady = savedInstanceState.getBoolean(PLAY_STATE);
        }

        videoView = rootView.findViewById(R.id.video_view_fragment);

        //Set up ExoPlayer
        initializePlayer();

        return rootView;
    }

    private void initializePlayer() {

        if(player == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            videoView.setPlayer(player);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                    Util.getUserAgent(getContext(), "Baking Recipe Step"));

            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoURL),
                    dataSourceFactory, new DefaultExtractorsFactory(), null, null);

            videoView.requestFocus();
            if (videoPosition != null) {
                player.prepare(videoSource, false, true);
                player.setPlayWhenReady(getPlayWhenReady);
                player.seekTo(videoPosition);
            } else {
                player.prepare(videoSource);
                player.setPlayWhenReady(getPlayWhenReady);
            }
            videoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        }
    }

    public void setUpVideo(String videoURL){
        this.videoURL=videoURL;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         if(player!=null){
             videoPosition = player.getCurrentPosition();
             player.stop();
             player.release();
             player=null;
         }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(player != null){
            videoPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            player=null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       if(player!=null && videoView!=null){
           player.setPlayWhenReady(getPlayWhenReady);
       } else if (player==null && videoView!=null){
           initializePlayer();
       }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putString(VIDEO_URL, videoURL);
       if(videoPosition!=null) currentState.putLong(POSITION, videoPosition);
       currentState.putBoolean(PLAY_STATE, getPlayWhenReady);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        getPlayWhenReady = playWhenReady; //The player state will change when user clicks play/pause
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}
}
