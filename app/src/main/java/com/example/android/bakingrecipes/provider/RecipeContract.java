package com.example.android.bakingrecipes.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {

    public static final String AUTHORITY = "com.example.android.bakingrecipes";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_RECIPES = "Recipes";


    public static final class RecipeEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "Baking_Recipes";
        public static final String COLUMN_RECIPE_NAME = "Recipe_Name";
        public static final String COLUMN_NO_OF_SERVINGS = "Servings";
        public static final String INGREDIENTS = "Ingredients";
    }
}
