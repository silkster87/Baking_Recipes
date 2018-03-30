package com.example.android.bakingrecipes.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RecipeContentProvider extends ContentProvider {

    private RecipeDbHelper mRecipeDbHelper;

    private static final int DIRECTORY_RECIPES = 100;
    private static final int DIRECTORY_RECIPES_ITEM = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES, DIRECTORY_RECIPES);
        sUriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES + "/#", DIRECTORY_RECIPES_ITEM);

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipeDbHelper = new RecipeDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mRecipeDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match){
            case(DIRECTORY_RECIPES):
                retCursor = db.query(RecipeContract.RecipeEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);

                return retCursor;

                default:
                    throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
       final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
       Uri returnUri;
       //inserting a single row of data when user favourited a recipe
        switch(sUriMatcher.match(uri)){
            case DIRECTORY_RECIPES: {
                long _id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, contentValues);

                if(_id > 0 ){
                    returnUri = ContentUris.withAppendedId(RecipeContract.BASE_CONTENT_URI, _id);
                } else {
                    throw new SQLException("Failed to insert data into: " + uri);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        //Deleting a recipe when a user unchecks a favourited recipe
        int itemsDeleted;

        switch (sUriMatcher.match(uri)){
            case(DIRECTORY_RECIPES):{
                itemsDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        if(itemsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
