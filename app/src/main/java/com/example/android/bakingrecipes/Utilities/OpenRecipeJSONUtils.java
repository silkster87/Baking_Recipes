package com.example.android.bakingrecipes.Utilities;


import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by Silky on 11/03/2018.
 */

public class OpenRecipeJSONUtils {

    public static JSONArray getJSONArrayOfRecipeResults(String jsonRecipeDataResponse) throws JSONException{

        JSONArray parsedRecipeResultsData;

        parsedRecipeResultsData = new JSONArray(jsonRecipeDataResponse);

        return parsedRecipeResultsData;
    }
}
