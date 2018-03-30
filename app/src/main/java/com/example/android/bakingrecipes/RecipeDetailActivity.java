package com.example.android.bakingrecipes;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.android.bakingrecipes.provider.RecipeContract;
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

    @Nullable @BindView(R.id.recipe_ingredients) TextView mRecipeIngredients;
    @Nullable @BindView(R.id.steps_recyclerView) RecyclerView mStepsRecyclerView;
    @Nullable @BindView(R.id.servings_title) TextView mServingsTextView;
    @BindView(R.id.fav_checkBox) CheckBox mFavRecipeCheckbox;

    //Views in the master detail flow. These are used if in tablet mode landscape
    @Nullable @BindView(R.id.servings_title_land) TextView mServingsLandTextView;
    @Nullable @BindView(R.id.recipe_ingredients_land) TextView mRecipeIngredientsLandTextView;

    private Recipe mRecipe;

    public static final String recipeStep = "recipeStep";
    public static final String recipeTitle = "recipeTitle";
    public static final String positionStepClicked = "positionStepClicked";
    private int stepNumber;
    public static final String STEP_NUMBER = "STEP_NUMBER";
    private InstructionFragment instructionFragment;
    private VideoFragment videoFragment;
    private ArrayList<Integer> historyOfSteps;
    private static final String HISTORY_OF_STEPS = "HISTORY_OF_STEPS";
    private MasterListFragment stepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

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
            stepNumber = savedInstanceState.getInt(STEP_NUMBER);
            historyOfSteps = savedInstanceState.getIntegerArrayList(HISTORY_OF_STEPS);
        }

        setTitle(mRecipe.getRecipeName());

        List<Ingredient> mIngredientList = mRecipe.getArrayOfIngredients();

        if(findViewById(R.id.master_list_container) == null){
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
           // stepFragment.setStepToBeHighlighted(stepNumber);
            stepFragment.setListOfSteps(listOfSteps);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.master_list_container, stepFragment)
                    .commit();

            //Set the RHS of the 2 pane mode
            //If activity is recreated then the fragments are destroyed. So will need to make new
            //instruction and video fragments.

            instructionFragment = new InstructionFragment();
            videoFragment = new VideoFragment();

            if(savedInstanceState == null) {
                //If we are going into recipe detail activity for first time set it to first step

                String videoURL = mRecipe.getArrayOfSteps().get(0).getmVideoURL();
                String thumbNailURL = mRecipe.getArrayOfSteps().get(0).getmThumbNailURL();
                String instructionForStep = mRecipe.getArrayOfSteps().get(0).getmDescription();
                stepNumber = 0;
                historyOfSteps.add(stepNumber);

                instructionFragment.setInstructionText(instructionForStep);

                //sometimes there is no videoURL or thumbnailURL
                if (thumbNailURL.equals("")) {
                    videoFragment.setUpVideo(videoURL);
                } else if(videoURL.equals("")){
                    videoFragment.setUpVideo(thumbNailURL);
                }
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.video_land_frag, videoFragment)
                        .add(R.id.instruction_frag, instructionFragment)
                        .commit();
            } else{
                //SavedInstance state is not null and we need to replace existing fragments.
                //If screen is rotated it will use this else block.

                String videoURL = mRecipe.getArrayOfSteps().get(stepNumber).getmVideoURL();
                String thumbnailURL = mRecipe.getArrayOfSteps().get(stepNumber).getmThumbNailURL();
                String instructionForStep = mRecipe.getArrayOfSteps().get(stepNumber).getmDescription();
                historyOfSteps.add(stepNumber);
                instructionFragment.setInstructionText(instructionForStep);

                if(thumbnailURL.equals("") && videoURL.equals("")){
                    //There is no video content to show
                        FragmentManager fragmentManagerNoURL = getSupportFragmentManager();
                        fragmentManagerNoURL.beginTransaction()
                                .replace(R.id.instruction_frag, instructionFragment)
                                .remove(videoFragment)
                                .addToBackStack(null)
                                .commit();
                }else { //setting up the video URL
                    if (thumbnailURL.equals("")) {
                        videoFragment.setUpVideo(videoURL);
                    } else if (videoURL.equals("")) {
                        videoFragment.setUpVideo(thumbnailURL);
                    }
                    FragmentManager fragManager = getSupportFragmentManager();
                    fragManager.beginTransaction()
                            .replace(R.id.video_land_frag, videoFragment)
                            .replace(R.id.instruction_frag, instructionFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }

    }

    private boolean findIfFavRecipe() {

        /*
        try{
            String mSelection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=?";
            Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI, null,
                    mSelection,
                    new String[]{Integer.toString(mRecipe.getmID())},
                    null, null);

            if(cursor != null && cursor.getCount() != 0){
                cursor.close();
                return true;
            }else {
                return false;
            }
        } catch (Exception e){
            Toast.makeText(getBaseContext(), "Error: Unable to make query", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
*/

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            String json = sharedPrefs.getString(mRecipe.getRecipeName(), "");

            if(json.equals("")){
                return false;
            }else {
                return true;
            }

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
            String thumbnailURL = step.getmThumbNailURL();
            String instructionOfStep = step.getmDescription();

            instructionFragment = new InstructionFragment();
            instructionFragment.setInstructionText(instructionOfStep);

            if (thumbnailURL.equals("") && videoURL.equals("")) {
                //There is no video content to show
                FragmentManager fragmentManagerNoURL = getSupportFragmentManager();
                fragmentManagerNoURL.beginTransaction()
                        .remove(videoFragment)
                        .replace(R.id.instruction_frag, instructionFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                videoFragment = new VideoFragment();
                if (thumbnailURL.equals("")) {
                    videoFragment.setUpVideo(videoURL);
                } else if (videoURL.equals("")) {
                    videoFragment.setUpVideo(thumbnailURL);
                }
                FragmentManager fragManagerWithURL = getSupportFragmentManager();
                fragManagerWithURL.beginTransaction()
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
            //add recipe to favourite recipes DB - will be shown in widget
           /*
            addFavRecipe(mRecipe.getRecipeName(), mRecipe.getmID(), mRecipe.getmServings(),
                    mRecipe.getArrayOfIngredients());
*/
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mRecipe);
            prefsEditor.putString(mRecipe.getRecipeName(), json);
            prefsEditor.apply();

            Toast.makeText(getBaseContext(), mRecipe.getRecipeName() + " added to Widgets. ", Toast.LENGTH_LONG).show();

        } else {
            //delete recipe from favourite recipes DB - will be removed in widget
            /*
        int itemsDeleted = getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, RecipeContract.RecipeEntry.COLUMN_RECIPE_ID+"=?",
               new String[]{Integer.toString(mRecipe.getmID())});

        if(itemsDeleted != 0){
            Toast.makeText(getBaseContext(), mRecipe.getRecipeName() + " removed from Widgets.",
                    Toast.LENGTH_LONG).show();
        }
        */

            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            prefsEditor.remove(mRecipe.getRecipeName());
            prefsEditor.apply();

            Toast.makeText(getBaseContext(), mRecipe.getRecipeName() + " removed from Widgets.",
                    Toast.LENGTH_LONG).show();

        }
    }

    /*
    private void addFavRecipe(String recipeName, int recipeID, int recipeServings, ArrayList<Ingredient> arrayOfIngredients) {

        ContentValues cv = new ContentValues();

        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipeName);
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, recipeID);
        cv.put(RecipeContract.RecipeEntry.COLUMN_NO_OF_SERVINGS, recipeServings);
        cv.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS, makeIngredientsString(arrayOfIngredients));

        ContentResolver resolver = getContentResolver();

        Uri insertedUri = resolver.insert(RecipeContract.RecipeEntry.CONTENT_URI, cv);

        if(insertedUri != null){
            Toast.makeText(getBaseContext(), recipeName + " added to Widgets. ", Toast.LENGTH_LONG).show();
        }

    }
*/
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
    }


    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if(count==0){
            super.onBackPressed();
        } else {
            historyOfSteps.remove(historyOfSteps.size()-1);
            stepNumber = historyOfSteps.get(historyOfSteps.size()-1);
            stepFragment.setStepToBeHighlighted(stepNumber);
            getFragmentManager().popBackStack();
        }
    }

}
