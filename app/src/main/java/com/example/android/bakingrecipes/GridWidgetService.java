package com.example.android.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingrecipes.provider.RecipeContract;

public class GridWidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }


}
 class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext=applicationContext;
    }

     @Override
     public void onCreate() {

     }

     //This method is called on start
     //and when notifyAppWidgetViewDataChanged() is called
     @Override
     public void onDataSetChanged() {
        //Get all favourited recipes
         Uri RECIPE_URI = RecipeContract.RecipeEntry.CONTENT_URI;
         if(mCursor != null) mCursor.close();
         mCursor = mContext.getContentResolver().query(
                 RECIPE_URI,
                 null,
                 null,
                 null,
                 null
         );

     }

     @Override
     public void onDestroy() {
        mCursor.close();
     }

     @Override
     public int getCount() {
        if(mCursor==null) return 0;
        return mCursor.getCount();
     }

     @Override
     public RemoteViews getViewAt(int position) {
        if( mCursor==null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        int recipeNameIndex = mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME);
        int servingsIndex = mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NO_OF_SERVINGS);
        int ingredientsIndex = mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS);

        String recipeName = mCursor.getString(recipeNameIndex);
        int noOfServings = mCursor.getInt(servingsIndex);
        String ingredients = mCursor.getString(ingredientsIndex);

        String servings = "Servings: " + Integer.toString(noOfServings);

        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.recipe_widget_provider);

        views.setTextViewText(R.id.widget_recipe_name, recipeName);
        views.setTextViewText(R.id.widget_servings, servings);
        views.setTextViewText(R.id.appwidget_text, ingredients);

        //Need to make an onClick fill in intent for each recipe item
         //When user clicks on an item it will open up Recipe Detail Activity and use the recipe ID
         //to make a query and find the Recipe object by its ID
         Bundle extras = new Bundle();

         int recipeIDIndex = mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);
         int recipe_ID = mCursor.getInt(recipeIDIndex);
         extras.putInt(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, recipe_ID);
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
         return 0;
     }

     @Override
     public long getItemId(int i) {
         return 0;
     }

     @Override
     public boolean hasStableIds() {
         return false;
     }
 }