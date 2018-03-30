package com.example.android.bakingrecipes;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.android.bakingrecipes.provider.RecipeContract;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeID, String recipeName, int noOfServings, String ingredients) {

        //Get current width to decide whether to use single or grid view
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
        if (width < 300) {
            rv = getSingleRecipeRemoteView(context, recipeID, recipeName, noOfServings, ingredients);
        } else {
            rv = getGridRecipeRemoteView(context);
        }

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }


    private static RemoteViews getSingleRecipeRemoteView(Context context, int recipeID, String recipeName, int noOfServings, String ingredients) {
        Intent intent;
        if(recipeID == RecipeContract.INVALID_RECIPE_ID){
            intent = new Intent(context, MainActivity.class);
        }else{
            intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(RecipeContract.EXTRA_RECIPE_ID, recipeID);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String servings = "Servings: " + Integer.toString(noOfServings);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.widget_recipe_name, recipeName);
        views.setTextViewText(R.id.widget_servings, servings);
        views.setTextViewText(R.id.appwidget_text, ingredients);

        views.setOnClickPendingIntent(R.id.recipe_widget, pendingIntent);

        return views;

    }


    private static RemoteViews getGridRecipeRemoteView(Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipeWidgetUpdateService.startActionUpdateRecipeWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

