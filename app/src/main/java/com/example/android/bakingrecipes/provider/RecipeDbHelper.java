package com.example.android.bakingrecipes.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.bakingrecipes.provider.RecipeContract.RecipeEntry.TABLE_NAME;

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "baking_recipes.db";

    private static final int DATABASE_VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_NO_OF_SERVINGS + " INTEGER NOT NULL, " +
                RecipeContract.RecipeEntry.INGREDIENTS + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
