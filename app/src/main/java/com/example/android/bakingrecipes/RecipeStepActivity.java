package com.example.android.bakingrecipes;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/* This RecipeStepActivity class displays in detail a specific recipe step that the user
had clicked on in the RecipeDetailActivity. It will display the video of the recipe step
if it has one, a full description of the step and buttons to navigate to the previous or next
step.
 */

public class RecipeStepActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.recipe_step_instructions) TextView mStepInstructions;
    @Nullable @BindView(R.id.previous_button) Button mPreviousButton;
    @Nullable @BindView(R.id.next_button) Button mNextButton;
    @Nullable @BindView(R.id.video_view) com.devbrackets.android.exomedia.ui.widget.VideoView  mVideoView;
    @Nullable @BindView(R.id.video_view_land) com.devbrackets.android.exomedia.ui.widget.VideoView mVideoViewLand;

    private Recipe mRecipe;
    private int stepArrayPosition;

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

        if(findViewById(R.id.video_view) != null) {
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

            String videoURL = mStep.getmVideoURL();
            String thumbNailURL = mStep.getmThumbNailURL();

            //Sometimes the thumbnail URL has a video but if we don't have any videos we can hide it
            if(!videoURL.equals("")) {
                mVideoView.setVideoURI(Uri.parse(videoURL));
                mVideoView.start();
            } else if (!thumbNailURL.equals("")){
                mVideoView.setVideoURI(Uri.parse(thumbNailURL));
                mVideoView.start();
            }else {
                mVideoView.setVisibility(View.GONE);
            }

            mStepInstructions.setText(mStep.getmDescription());

        } else { //If we are in landscape mode use full screen
            Step mStep = getIntent().getParcelableExtra(RecipeDetailActivity.recipeStep);
            String recipeTitle = getIntent().getStringExtra(RecipeDetailActivity.recipeTitle);
            String videoURL = mStep.getmVideoURL();
            String thumbNailURL = mStep.getmThumbNailURL();
            setTitle(recipeTitle);

            //Sometimes the thumbnail URL has a video but if we don't have any videos we can hide it
            if(!videoURL.equals("")) {
                mVideoViewLand.setVideoURI(Uri.parse(videoURL));
                mVideoViewLand.start();
            } else if (!thumbNailURL.equals("")){
                mVideoViewLand.setVideoURI(Uri.parse(thumbNailURL));
                mVideoViewLand.start();
            }else {
                mVideoViewLand.setVisibility(View.GONE);
            }

        }
    }



    //The Exomedia library from dev brackets was used - it is essentially a wrapper around ExoPlayer.
    //This simplifies setting up the video since we are only querying off a video URL

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView != null) {
            mVideoView.release();
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

}
