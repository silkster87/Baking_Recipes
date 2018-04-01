package com.example.android.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingrecipes.RecipeObjects.Ingredient;
import com.example.android.bakingrecipes.RecipeObjects.Recipe;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/*
* In this GridWidgetService we will get an instance of a GridRemoteViewsFactory. SharedPreferences
* are used in updating the Widget data.
* */
public class GridWidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }


}
 class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Map<String, ?> mKeys;
    private ArrayList<Recipe> mRecipeList;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext=applicationContext;
    }

     @Override
     public void onCreate() { }

     //This method is called on start
     //and when notifyAppWidgetViewDataChanged() is called
     @Override
     public void onDataSetChanged() {
        //Get all favourited recipes from shared preferences
         SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
         mKeys = sharedPrefs.getAll();

         makeListOfRecipes();
     }

     private void makeListOfRecipes() {

        mRecipeList = new ArrayList<>();

         for(Map.Entry<String, ?> entry : mKeys.entrySet()) {
             String json = entry.getValue().toString();
             Gson gson = new Gson();
             Recipe mRecipe = gson.fromJson(json, Recipe.class);

             mRecipeList.add(mRecipe);
         }
     }

     @Override
     public void onDestroy() { }

     @Override
     public int getCount() {
        if(mKeys==null) return 0;
        return mKeys.size();
     }

     @Override
     public RemoteViews getViewAt(int position) {


         RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);

            Recipe mRecipe = mRecipeList.get(position);

            String recipeName = mRecipe.getRecipeName();
            int noOfServings = mRecipe.getmServings();
            ArrayList<Ingredient> mIngredientList = mRecipe.getArrayOfIngredients();

            String servings = "Servings: " + Integer.toString(noOfServings);

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

            views.setTextViewText(R.id.widget_recipe_name, recipeName);
            views.setTextViewText(R.id.widget_servings, servings);
            views.setTextViewText(R.id.appwidget_text, ingredients);

            //Fill in the onClick PendingIntent template using specific recipe for each item
            Bundle extras = new Bundle();
            extras.putParcelable(MainActivity.recipeBundle, mRecipe);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.recipe_widget, fillInIntent);


        return views;
     }

     @Override
     public RemoteViews getLoadingView() {
         return null;
     }

     @Override
     public int getViewTypeCount() {
         return 1;
     }

     @Override
     public long getItemId(int i) {
         return i;
     }

     @Override
     public boolean hasStableIds() {
         return true;
     }
 }