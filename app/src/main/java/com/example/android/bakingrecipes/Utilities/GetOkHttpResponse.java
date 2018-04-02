package com.example.android.bakingrecipes.Utilities;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Silky on 15/03/2018.
 */

public class GetOkHttpResponse {

    private String url;

    private OkHttpClient client = new OkHttpClient();

    public final static String BAKING_RECIPES_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public GetOkHttpResponse(String url){
        this.url = url;
    }

    public String run() throws IOException{
        Request request = new Request.Builder().url(url).build();

        try {Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


}
