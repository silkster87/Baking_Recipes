package com.example.android.bakingrecipes;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.support.annotation.Nullable;

/*
* This is a service that the RecipeWidgetProvider will call on its onUpdate() method.
* When it handles the intent it will update the widgets.
* */

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

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);

        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds);

    }
}
