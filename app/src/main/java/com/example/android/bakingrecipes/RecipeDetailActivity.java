package com.example.android.bakingrecipes;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import com.example.android.bakingrecipes.RecipeObjects.Ingredient;
import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.example.android.bakingrecipes.UI.InstructionFragment;
import com.example.android.bakingrecipes.UI.MasterListAdapter;
import com.example.android.bakingrecipes.UI.MasterListFragment;
import com.example.android.bakingrecipes.UI.VideoFragment;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/*This class will display the selected Recipe from the user in terms of the steps and ingredients
for the recipe. The user can click on a specific step for more detail. This will then launch a
recipe step activity.
* */

public class RecipeDetailActivity extends AppCompatActivity implements MasterListFragment.OnStepItemSelectedListener{

    @Nullable @BindView(R.id.recipe_ingredients) TextView mRecipeIngredients;
    @Nullable @BindView(R.id.steps_recyclerView) RecyclerView mStepsRecyclerView;
    @Nullable @BindView(R.id.servings_title) TextView mServingsTextView;

    //Views in the master detail flow. These are used if in tablet mode landscape
    @Nullable @BindView(R.id.servings_title_land) TextView mServingsLandTextView;
    @Nullable @BindView(R.id.recipe_ingredients_land) TextView mRecipeIngredientsLandTextView;

    private Recipe mRecipe;
    private Boolean mTwoPane;

    public static final String recipeStep = "recipeStep";
    public static final String recipeTitle = "recipeTitle";
    public static final String positionStepClicked = "positionStepClicked";
    public static final String STEP_ITEM_VIDEO = "step_item_video";
    public static final String STEP_ITEM_INSTRUCTION = "step_item_instruction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

        mRecipe = getIntent().getParcelableExtra(MainActivity.recipeBundle);

        setTitle(mRecipe.getRecipeName());

        List<Ingredient> mIngredientList = mRecipe.getArrayOfIngredients();

        if(findViewById(R.id.master_list_container) == null){
            mTwoPane = false; //Not in 2 pane mode - in phone view

        mStepsRecyclerView.setHasFixedSize(true);
        mStepsRecyclerView.setNestedScrollingEnabled(false);
        mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        StepsAdapter mStepsAdapter = new StepsAdapter(new StepsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Step stepItem, int position) {
                //start new step activity with video of step
                Intent intent = new Intent(getApplicationContext(), RecipeStepActivity.class);
                intent.putExtra(recipeStep, stepItem);
                intent.putExtra(MainActivity.recipeBundle, mRecipe);
                intent.putExtra(recipeTitle, mRecipe.getRecipeName());
                intent.putExtra(positionStepClicked, position);
                startActivity(intent);
            }
        }, mRecipe.getArrayOfSteps());

        mStepsRecyclerView.setAdapter(mStepsAdapter);

        StringBuilder builder = new StringBuilder();

            for(int i=0; i<mIngredientList.size(); i++){

                builder.append(mIngredientList.get(i).getIngredient())
                        .append(", ")
                        .append(mIngredientList.get(i).getmQuantity())
                        .append(" ")
                        .append((mIngredientList.get(i).getmMeasure()))
                        .append("\n");
            }

            String ingredients = builder.toString();
        mRecipeIngredients.setText(ingredients);

        String mServings = " " + Integer.toString(mRecipe.getmServings());
        mServingsTextView.append(mServings);

        } else {
            mTwoPane = true; //We are in 2 pane mode

            //On the LHS pane, we want the scroll view to start at the top
            findViewById(R.id.linear_layout_in_scroll_view).requestFocus();

            //Setting the LHS of the master detail flow
            StringBuilder builder = new StringBuilder();

            for(int i=0; i<mIngredientList.size(); i++){

                builder.append(mIngredientList.get(i).getIngredient())
                        .append(", ")
                        .append(mIngredientList.get(i).getmQuantity())
                        .append(" ")
                        .append((mIngredientList.get(i).getmMeasure()))
                        .append("\n");
            }

            String ingredients = builder.toString();
            mRecipeIngredientsLandTextView.setText(ingredients);
            String mServingsLand = " " + Integer.toString(mRecipe.getmServings());
            mServingsLandTextView.append(mServingsLand);

            List<Step> listOfSteps = mRecipe.getArrayOfSteps();
            MasterListFragment stepFragment = new MasterListFragment();
            stepFragment.setListOfSteps(listOfSteps);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.master_list_container, stepFragment)
                    .commit();

            //Set the RHS of the 2 pane mode

            VideoFragment videoFragment = new VideoFragment();
            //If initially no step is selected go to first step

            if(savedInstanceState == null) {

                String videoURL = mRecipe.getArrayOfSteps().get(0).getmVideoURL();
                String thumbNailURL = mRecipe.getArrayOfSteps().get(0).getmThumbNailURL();

                if (thumbNailURL.equals("")) {
                    videoFragment.setUpVideo(videoURL);
                } else {
                    videoFragment.setUpVideo(thumbNailURL);
                }

            } else{
                String videoURL = savedInstanceState.getString(STEP_ITEM_VIDEO);
                videoFragment.setUpVideo(videoURL);
            }

            InstructionFragment instructionFragment = new InstructionFragment();
            //If initially no step is selected go to the first instruction
            if(savedInstanceState == null){
                String instructionForStep = mRecipe.getArrayOfSteps().get(0).getmDescription();
                instructionFragment.setInstructionText(instructionForStep);
            } else {
                String instructionForStep = savedInstanceState.getString(STEP_ITEM_INSTRUCTION);
                instructionFragment.setInstructionText(instructionForStep);
            }

            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.video_land_frag, videoFragment)
                    .add(R.id.instruction_frag, instructionFragment).commit();

        }

    }

    @Override
    public void onStepItemSelected(Step step) {
        //When user clicks on a step, we want the video and instruction fragments to update according
        //to the step. Make new fragments and replace them.
        String videoURL = step.getmVideoURL();
        String thumbnailURL = step.getmThumbNailURL();
        String instructionOfStep = step.getmDescription();

        VideoFragment videoFragment = new VideoFragment();
        InstructionFragment instructionFragment = new InstructionFragment();

        if(thumbnailURL.equals("")){
            videoFragment.setUpVideo(videoURL);
        }else{
            videoFragment.setUpVideo(thumbnailURL);
        }

        instructionFragment.setInstructionText(instructionOfStep);

        FragmentManager fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.video_land_frag, videoFragment)
                .replace(R.id.instruction_frag, instructionFragment).commit();
    }
}
