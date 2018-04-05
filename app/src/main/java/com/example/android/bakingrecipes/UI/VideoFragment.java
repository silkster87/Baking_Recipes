package com.example.android.bakingrecipes.UI;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingrecipes.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
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

public class VideoFragment extends Fragment {

    private String videoURL;
    private SimpleExoPlayerView videoView;
    private SimpleExoPlayer player;
    public static final String VIDEO_URL = "video_url";
    private static final String VIDEO_POSITION = "video_position";


    public VideoFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.video_fragment_layout, container, false);
        long currentPosition = 0;

        if(savedInstanceState != null){
            videoURL = savedInstanceState.getString(VIDEO_URL);
            currentPosition = savedInstanceState.getLong(VIDEO_POSITION);
        }
        videoView = rootView.findViewById(R.id.video_view_fragment);

        //Set up ExoPlayer
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        videoView.setPlayer(player);
        videoView.setKeepScreenOn(true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "Baking Recipe Step"));

        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoURL),
                dataSourceFactory, new DefaultExtractorsFactory(), null, null);

        player.prepare(videoSource);
        videoView.requestFocus();

        if(currentPosition != 0){
            player.seekTo(currentPosition);
        }
        player.setPlayWhenReady(true);
        return rootView;
    }

    public void setUpVideo(String videoURL){
        this.videoURL=videoURL;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         if(player!=null){
             player.release();
         }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(player != null){
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       if(player!=null){
           player.setPlayWhenReady(true);
       }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(VIDEO_URL, videoURL);
        long videoCurrentPosition = player.getCurrentPosition();

        currentState.putLong(VIDEO_POSITION, videoCurrentPosition);
    }
}
