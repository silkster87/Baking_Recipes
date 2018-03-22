package com.example.android.bakingrecipes;

import android.content.Intent;
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
import com.example.android.bakingrecipes.UI.MasterListFragment;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/*This class will display the selected Recipe from the user in terms of the steps and ingredients
for the recipe. The user can click on a specific step for more detail. This will then launch a
recipe step activity.
* */

public class RecipeDetailActivity extends AppCompatActivity implements MasterListFragment.OnStepItemSelectedListener{

    @BindView(R.id.recipe_ingredients) TextView mRecipeIngredients;
    @BindView(R.id.steps_recyclerView) RecyclerView mStepsRecyclerView;
    @BindView(R.id.servings_title) TextView mServingsTextView;

    //Views in the master detail flow. These are used if in tablet mode landscape
    @BindView(R.id.servings_title_land) TextView mServingsLandTextView;
    @BindView(R.id.recipe_ingredients_land) TextView mRecipeIngredientsLandTextView;

    private Recipe mRecipe;
    private Boolean mTwoPane;

    public static final String recipeStep = "recipeStep";
    public static final String recipeTitle = "recipeTitle";
    public static final String positionStepClicked = "positionStepClicked";

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

            MasterListFragment stepFragment = new MasterListFragment();
            stepFragment.setRecipeData(mRecipe);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.master_list_container, stepFragment)
                    .commit();

            //Set the RHS of the 2 pane mode



        }

    }

    @Override
    public void onStepItemSelected(Step step) {
        String videoURL = step.getmVideoURL();
        String thumbnailURL = step.getmThumbNailURL();
        String instructionOfStep = step.getmDescription();
    }
}
