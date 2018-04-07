package com.example.android.bakingrecipes;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/* This RecipeStepActivity class displays in detail a specific recipe step that the user
had clicked on in the RecipeDetailActivity. It will display the video of the recipe step
if it has one, a full description of the step and buttons to navigate to the previous or next
step.
 */

public class RecipeStepActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    @Nullable @BindView(R.id.recipe_step_instructions) TextView mStepInstructions;
    @Nullable @BindView(R.id.previous_button) Button mPreviousButton;
    @Nullable @BindView(R.id.next_button) Button mNextButton;
    @Nullable @BindView(R.id.player_view) SimpleExoPlayerView playerView;
    @Nullable @BindView(R.id.player_view_land) SimpleExoPlayerView playerViewLand;
    @Nullable @BindView(R.id.instructions_land) TextView mStepInstrLand;

    private Recipe mRecipe;
    private int stepArrayPosition;
    private SimpleExoPlayer player;
    private String VIDEO_POSITION = "Video_Position";
    private String PLAY_STATE = "Play_State";
    private String videoURL;
    private Boolean getPlayWhenReady = true;

    @Nullable private Long videoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getSupportActionBar().hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_recipe_step);
        ButterKnife.bind(this);

        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(VIDEO_POSITION))
            videoPosition = savedInstanceState.getLong(VIDEO_POSITION);
            if(savedInstanceState.containsKey(PLAY_STATE))
                getPlayWhenReady = savedInstanceState.getBoolean(PLAY_STATE);
        }

        if(getResources().getBoolean(R.bool.portraitMode)) {
            //If we are in portrait mode we can use next and previous buttons

            mRecipe = getIntent().getParcelableExtra(MainActivity.recipeBundle);

            //The stepArrayPosition is an integer that will help navigate through the steps array should
            //the user click on previous or next. This is necessary because sometimes the array position
            //doesn't always equal the step ID.

            stepArrayPosition = getIntent().getIntExtra(RecipeDetailActivity.positionStepClicked, 0);

            if (stepArrayPosition == mRecipe.getArrayOfSteps().size() - 1) {
                mNextButton.setVisibility(View.GONE);
            } else if (stepArrayPosition == 0) {
                mPreviousButton.setVisibility(View.GONE);
            }
            Step mStep = getIntent().getParcelableExtra(RecipeDetailActivity.recipeStep);
            String recipeTitle = getIntent().getStringExtra(RecipeDetailActivity.recipeTitle);
            setTitle(recipeTitle);
            videoURL = mStep.getmVideoURL();

            if(!TextUtils.isEmpty(videoURL)) {
                setUpExoPlayerVideo(videoURL, playerView);
            } else {
                playerView.setVisibility(View.GONE);
            }

            mStepInstructions.setText(mStep.getmDescription());

        } else { //If we are in landscape mode use full screen
            Step mStep = getIntent().getParcelableExtra(RecipeDetailActivity.recipeStep);
            String recipeTitle = getIntent().getStringExtra(RecipeDetailActivity.recipeTitle);
            String videoURL = mStep.getmVideoURL();
            String instructions = mStep.getmDescription();
            setTitle(recipeTitle);

            if(!TextUtils.isEmpty(videoURL)) {
                mStepInstrLand.setVisibility(View.GONE);
                setUpExoPlayerVideo(videoURL, playerViewLand);
            } else { //There is no video to show so set instructions instead.
               playerViewLand.setVisibility(View.GONE);
                mStepInstrLand.setVisibility(View.VISIBLE);
                mStepInstrLand.setText(instructions);
            }
        }
    }

    private void setUpExoPlayerVideo(String videoURL, SimpleExoPlayerView playerView){
        //Set up ExoPlayer - default trackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance( this, trackSelector, loadControl);
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Baking Recipe"));

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //MediaSource for the media to be played
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(videoURL),
                dataSourceFactory, extractorsFactory, null, null);

        playerView.requestFocus();

        player.addListener(this);

        if(videoPosition !=null){
            player.prepare(videoSource, false, true);
            player.setPlayWhenReady(getPlayWhenReady);
            player.seekTo(videoPosition);
        } else {
            player.prepare(videoSource);
            player.setPlayWhenReady(getPlayWhenReady);
        }
        //Fill the player view fully but maintain aspect ratio
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player != null){
            videoPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            player=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player == null && playerView !=null) {
            setUpExoPlayerVideo(videoURL, playerView);
            player.setPlayWhenReady(getPlayWhenReady);
            if(videoPosition!=null) {
                player.seekTo(videoPosition);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player != null){
            videoPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            player=null;
        }

    }

    @Optional @OnClick(R.id.next_button)
    public void nextButton(){
        stepArrayPosition += 1;
            startNewStepActivity(stepArrayPosition);
    }

    @Optional @OnClick(R.id.previous_button)
    public void previousButton(){
        stepArrayPosition -= 1;
        startNewStepActivity(stepArrayPosition);
    }

    //When the user clicks on previous or next, we override the current intent with the new
    //extras so that when this same activity is started again it can retrieve its own data.

    private void startNewStepActivity(int stepID) {
        Step newStep = mRecipe.getArrayOfSteps().get(stepID);
        Intent intent = getIntent();
        intent.putExtra(RecipeDetailActivity.recipeStep, newStep);
        intent.putExtra(MainActivity.recipeBundle, mRecipe);
        intent.putExtra(RecipeDetailActivity.positionStepClicked, stepArrayPosition);
        finish();
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(videoPosition!=null) outState.putLong(VIDEO_POSITION, videoPosition);
        outState.putBoolean(PLAY_STATE, getPlayWhenReady);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        getPlayWhenReady = playWhenReady; //The player state will change when user clicks play/pause
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}


}
