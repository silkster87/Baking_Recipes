package com.example.android.bakingrecipes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.android.bakingrecipes.RecipeObjects.Ingredient;
import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.example.android.bakingrecipes.RecipeObjects.Step;
import com.example.android.bakingrecipes.Utilities.GetOkHttpResponse;
import com.example.android.bakingrecipes.Utilities.NetworkUtils;
import com.example.android.bakingrecipes.Utilities.OpenRecipeJSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/*This Main Activity will display a card list view of all the recipes from the JSON query.
* The user will click on a recipe which will open up a new RecipeDetailActivity.
*
* The library OkHttp was used to get the JSON into a string and then a Recipe object
* made from that JSON and passed into a Parcelable extra when user clicks on a Recipe to the
* next activity.
*
* */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    private RecyclerView mRecipeRecyclerView;
    private RecipesAdapter mRecipesAdapter;
    private ArrayList<Recipe> arrayOfRecipes;

    public static final String recipeBundle = "recipeDetails";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Udacity's Baking App");

    //Need to set the recycler view depending on whether it is phone or tablet
        if(findViewById(R.id.recipe_recyclerView) != null) {

            mRecipeRecyclerView = findViewById(R.id.recipe_recyclerView);
            mRecipeRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecipeRecyclerView.setLayoutManager(mLayoutManager);

        } else if (findViewById(R.id.recipe_recyclerView_tablet) != null){

            mRecipeRecyclerView = findViewById(R.id.recipe_recyclerView_tablet);
            mRecipeRecyclerView.setHasFixedSize(true);
            mRecipeRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mRecipesAdapter = new RecipesAdapter(new RecipesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Recipe recipeItem) {

                Class dest = RecipeDetailActivity.class;
                Intent intent = new Intent(getApplicationContext(), dest);
                intent.putExtra(recipeBundle, recipeItem);
                startActivity(intent);
            }
        });

        mRecipeRecyclerView.setAdapter(mRecipesAdapter);
        LoaderManager.LoaderCallbacks<ArrayList<Recipe>> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(0, null,  callback);

    }
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int i, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Recipe>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<Recipe> loadInBackground() {

                try{
                    GetOkHttpResponse getOkHttpResponse = new GetOkHttpResponse(NetworkUtils.BAKING_RECIPES_URL);
                    String jsonRecipeDataResponse = getOkHttpResponse.run();
                    JSONArray jsonArrayRecipeResult = OpenRecipeJSONUtils.getJSONArrayOfRecipeResults(jsonRecipeDataResponse);

                    if (jsonArrayRecipeResult==null) throw new AssertionError();
                    arrayOfRecipes = new ArrayList<>();

                    //We want to loop through the array list of recipes.

                    for(int i=0; i<jsonArrayRecipeResult.length(); i++){

                        JSONObject recipeJSONObject = jsonArrayRecipeResult.getJSONObject(i);

                        int recipeID = recipeJSONObject.getInt("id");
                        String recipeName = recipeJSONObject.getString("name");
                        int recipeServings = recipeJSONObject.getInt("servings");
                        String recipeImage = recipeJSONObject.getString("image");

                        JSONArray arrayOfIngredients = recipeJSONObject.getJSONArray("ingredients");

                        ArrayList<Ingredient> ingredientsList = new ArrayList<>();

                        //We want to loop through the array list of ingredients
                        for(int n=0; n<arrayOfIngredients.length();n++){

                            JSONObject ingredientJSONObject = arrayOfIngredients.getJSONObject(n);

                            int ingredientQuantity = ingredientJSONObject.getInt("quantity");
                            String ingredientMeasure = ingredientJSONObject.getString("measure");
                            String ingredientName = ingredientJSONObject.getString("ingredient");

                            Ingredient ingredientItem = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);

                            ingredientsList.add(ingredientItem);
                        }

                        JSONArray arrayOfSteps = jsonArrayRecipeResult.getJSONObject(i).getJSONArray("steps");

                        ArrayList<Step> listOfSteps = new ArrayList<>();

                        //We want to loop through the array list of steps
                        for(int m=0; m<arrayOfSteps.length(); m++){

                            JSONObject stepJsonObject = arrayOfSteps.getJSONObject(m);

                            int stepID = stepJsonObject.getInt("id");
                            String stepShortDesc = stepJsonObject.getString("shortDescription");
                            String stepDesc = stepJsonObject.getString("description");
                            String stepVideoURL = stepJsonObject.getString("videoURL");
                            String stepThumbnailURL = stepJsonObject.getString("thumbnailURL");

                            Step stepItem = new Step(stepID, stepShortDesc, stepDesc, stepVideoURL, stepThumbnailURL);

                            listOfSteps.add(stepItem);
                        }

                        Recipe recipeItem = new Recipe(recipeID, recipeName, ingredientsList, listOfSteps,
                                recipeServings, recipeImage);

                        arrayOfRecipes.add(recipeItem);
                    }

                    return arrayOfRecipes;

                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Recipe> data) {
                arrayOfRecipes = data;
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        mRecipesAdapter.setRecipeData(recipes);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }
}
