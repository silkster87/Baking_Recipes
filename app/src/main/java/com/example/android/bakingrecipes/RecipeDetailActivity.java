package com.example.android.bakingrecipes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingrecipes.RecipeObjects.Ingredient;
import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.example.android.bakingrecipes.UI.InstructionFragment;
import com.example.android.bakingrecipes.UI.MasterListFragment;
import com.example.android.bakingrecipes.UI.VideoFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*This class will display the selected Recipe from the user in terms of the steps and ingredients
for the recipe. The user can click on a specific step for more detail. This will then launch a
recipe step activity.
* */

public class RecipeDetailActivity extends AppCompatActivity implements MasterListFragment.OnStepItemSelectedListener{

    @Nullable @BindView(R.id.recipe_ingredients)  TextView mRecipeIngredients;
    @Nullable @BindView(R.id.steps_recyclerView)  RecyclerView mStepsRecyclerView;
    @Nullable @BindView(R.id.servings_title)  TextView mServingsTextView;
    @BindView(R.id.fav_checkBox)  CheckBox mFavRecipeCheckbox;

    //Views in the master detail flow. These are used if in tablet mode landscape
    @Nullable @BindView(R.id.servings_title_land)  TextView mServingsLandTextView;
    @Nullable @BindView(R.id.recipe_ingredients_land) TextView mRecipeIngredientsLandTextView;

    private Recipe mRecipe;

    private static final String VIDEO_POSITION = "Video_Position";
    public static final String recipeStep = "recipeStep";
    public static final String recipeTitle = "recipeTitle";
    public static final String positionStepClicked = "positionStepClicked";
    private int stepNumber;
    private static final String STEP_NUMBER = "STEP_NUMBER";
    private InstructionFragment instructionFragment;
    private VideoFragment videoFragment;
    private ArrayList<Integer> historyOfSteps;
    private static final String HISTORY_OF_STEPS = "HISTORY_OF_STEPS";
    private MasterListFragment stepFragment;
    private FragmentManager fragmentManager;
    @Nullable private Long currentVideoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        ActionBar ab = getSupportActionBar();

        if(ab!=null) ab.setDisplayHomeAsUpEnabled(true);

        mRecipe = getIntent().getParcelableExtra(MainActivity.recipeBundle);

        historyOfSteps = new ArrayList<>();

        boolean isFavRecipe = findIfFavRecipe();

        if(isFavRecipe){
            mFavRecipeCheckbox.setChecked(true);
        }else {
            mFavRecipeCheckbox.setChecked(false);
        }

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STEP_NUMBER)){
                stepNumber = savedInstanceState.getInt(STEP_NUMBER);
            }
            if(savedInstanceState.containsKey(HISTORY_OF_STEPS)){
                historyOfSteps = savedInstanceState.getIntegerArrayList(HISTORY_OF_STEPS);
            }
            if(savedInstanceState.containsKey(VIDEO_POSITION)){
                currentVideoPosition = savedInstanceState.getLong(VIDEO_POSITION);
            }
        }else{
            stepNumber = 0;
        }

        setTitle(mRecipe.getRecipeName());

        List<Ingredient> mIngredientList = mRecipe.getArrayOfIngredients();

        if(!getResources().getBoolean(R.bool.isTablet)){
            //Not in 2 pane mode - in phone view

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
           //We are in 2 pane mode

            //On the LHS pane, we want the scroll view to start at the top
            findViewById(R.id.linear_layout_in_scroll_view).requestFocus();

            //Setting the LHS of the master detail flow
            String ingredients = makeIngredientsString(mIngredientList);

            mRecipeIngredientsLandTextView.setText(ingredients);
            String mServingsLand = " " + Integer.toString(mRecipe.getmServings());
            mServingsLandTextView.append(mServingsLand);

            List<Step> listOfSteps = mRecipe.getArrayOfSteps();
            stepFragment = new MasterListFragment();
            stepFragment.setListOfSteps(listOfSteps);


            fragmentManager.beginTransaction()
                    .add(R.id.master_list_container, stepFragment)
                    .commit();

            //Set the RHS of the 2 pane mode
            //If activity is recreated then the fragments are destroyed. So will need to make new
            //instruction and video fragments.

            instructionFragment = new InstructionFragment();

            String videoURL = mRecipe.getArrayOfSteps().get(stepNumber).getmVideoURL();
            String instructionForStep = mRecipe.getArrayOfSteps().get(stepNumber).getmDescription();
            historyOfSteps.add(stepNumber);
            instructionFragment.setInstructionText(instructionForStep);

            if(savedInstanceState==null){
                videoFragment = new VideoFragment();
            } else {
                videoFragment = (VideoFragment) fragmentManager.findFragmentById(R.id.video_land_frag);
            }

                    if (TextUtils.isEmpty(videoURL)) {
                        //There is no video content to show
                        fragmentManager.beginTransaction()
                                .replace(R.id.instruction_frag, instructionFragment)
                                .remove(videoFragment)
                                .addToBackStack(null)
                                .commit();
                    } else { //setting up the video URL
                        if(savedInstanceState!=null){
                                videoFragment.setPosition(currentVideoPosition);
                        }
                        videoFragment.setUpVideo(videoURL);

                        if(R.id.video_land_frag != 0) {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.video_land_frag, videoFragment)
                                    .replace(R.id.instruction_frag, instructionFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            fragmentManager.beginTransaction()
                                    .add(R.id.video_land_frag, videoFragment)
                                    .replace(R.id.instruction_frag, instructionFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
        }
    }

    private boolean findIfFavRecipe() {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            String json = sharedPrefs.getString(mRecipe.getRecipeName(), "");
        return !json.equals("");
    }

    @Override
    public void onStepItemSelected(Step step) {
        //When user clicks on a step, we want the video and instruction fragments to update according
        //to the step. Make new fragments and replace them.

        int newStepNumber = mRecipe.getArrayOfSteps().indexOf(step);

        //If user clicks on an step that is already showing then it is unnecessary to make changes
        if(newStepNumber != stepNumber) {
            
            historyOfSteps.add(newStepNumber);
            
            stepNumber = newStepNumber;
            String videoURL = step.getmVideoURL();
            String instructionOfStep = step.getmDescription();

            instructionFragment = new InstructionFragment();
            instructionFragment.setInstructionText(instructionOfStep);

            if (TextUtils.isEmpty(videoURL)) {
                //There is no video content to show
                fragmentManager.beginTransaction()
                        .remove(videoFragment)
                        .replace(R.id.instruction_frag, instructionFragment)
                        .addToBackStack(null)
                        .commit();
                videoFragment = null;
            } else {
                videoFragment = new VideoFragment();
                videoFragment.setUpVideo(videoURL);

                fragmentManager.beginTransaction()
                        .replace(R.id.video_land_frag, videoFragment)
                        .replace(R.id.instruction_frag, instructionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }


    @OnClick(R.id.fav_checkBox)
    public void addOrRemoveRecipeWidget(){
        View view = findViewById(R.id.fav_checkBox);
        boolean checked = ((CheckBox) view).isChecked();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        if(checked){
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mRecipe);
            prefsEditor.putString(mRecipe.getRecipeName(), json);
            prefsEditor.apply();
            Toast.makeText(getBaseContext(), mRecipe.getRecipeName() + " added to Widgets. ", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            prefsEditor.remove(mRecipe.getRecipeName());
            prefsEditor.apply();
            Toast.makeText(getBaseContext(), mRecipe.getRecipeName() + " removed from Widgets.",
                    Toast.LENGTH_LONG).show();
        }
        //Update the appWidgets
        Intent intent = new Intent(this, RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private String makeIngredientsString(List<Ingredient> mIngredientList){

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<mIngredientList.size(); i++){
            builder.append(mIngredientList.get(i).getIngredient())
                    .append(", ")
                    .append(mIngredientList.get(i).getmQuantity())
                    .append(" ")
                    .append((mIngredientList.get(i).getmMeasure()))
                    .append("\n");
        }
        return builder.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_NUMBER, stepNumber);
        outState.putIntegerArrayList(HISTORY_OF_STEPS, historyOfSteps);

        if(videoFragment!=null) {
            long currentPosition = videoFragment.getCurrentPosition();
            outState.putLong(VIDEO_POSITION, currentPosition);
        }

    }


}
