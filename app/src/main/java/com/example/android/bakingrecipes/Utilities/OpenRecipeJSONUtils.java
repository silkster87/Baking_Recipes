package com.example.android.bakingrecipes.Utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

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
