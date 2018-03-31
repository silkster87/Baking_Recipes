package com.example.android.bakingrecipes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Intent intent = new Intent(context, GridWidgetService.class);
        rv.setRemoteAdapter(R.id.widget_grid_view, intent);

        //Set the RecipeDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT
                ,appIntent, 0);
        rv.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        //Set to empty view if no recipes are added to widgets
        rv.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
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


    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

