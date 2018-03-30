package com.example.android.bakingrecipes;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.bakingrecipes.provider.RecipeContract;

public class RecipeWidgetUpdateService extends IntentService{

   public static final String ACTION_UPDATE_RECIPE_WIDGETS = "update_recipe_widgets";

    public RecipeWidgetUpdateService() {
        super("RecipeWidgetUpdateService");
    }

    public static void startActionUpdateRecipeWidgets(Context context) {
        Intent intent = new Intent(context, RecipeWidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_RECIPE_WIDGETS.equals(action)){
                handleActionUpdateRecipeWidgets();
            }
        }

    }

    public void handleActionUpdateRecipeWidgets() {
        Uri RECIPE_URI = RecipeContract.RecipeEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(
                RECIPE_URI,
                null,
                null,
                null,
                null
        );

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        if(cursor != null && cursor.getCount() > 0 ){
            cursor.moveToFirst();

            for(int i = 0; i < appWidgetIds.length; i++) {
                while (cursor.moveToNext()) {
                    int recipeNameIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME);
                    int recipeIDIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);
                    int noOfServingsIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NO_OF_SERVINGS);
                    int ingredientsIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS);

                    String recipeName = cursor.getString(recipeNameIndex);
                    int recipeID = cursor.getInt(recipeIDIndex);
                    int noOfServings = cursor.getInt(noOfServingsIndex);
                    String ingredients = cursor.getString(ingredientsIndex);

                    RecipeWidgetProvider.updateAppWidget(getBaseContext(), appWidgetManager, appWidgetIds[i], recipeID, recipeName, noOfServings, ingredients);
                }
            }
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
            cursor.close();
        }

    }
}
