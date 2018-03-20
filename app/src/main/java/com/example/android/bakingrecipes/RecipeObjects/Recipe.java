package com.example.android.bakingrecipes.RecipeObjects;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**Each recipe has an id, a recipe name, a series of ingredients (put into an ingredients array),
 * a series of steps (put into a steps array), a thumbnail URL, servings and an image URL. Not all
 * recipes will have image or thumbnail URL.
 * Created by Silky on 10/03/2018.
 */

public class Recipe implements Parcelable{


    private int mID;

    private String recipeName;

    private ArrayList<Ingredient> arrayOfIngredients = new ArrayList<>();

    private ArrayList<Step> arrayOfSteps = new ArrayList<>();

    private int mServings;

    private String mImageURL;


    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps,
                  int servings, String imageURL){
        this.mID = id;
        this.recipeName = name;
        this.arrayOfIngredients = ingredients;
        this.arrayOfSteps = steps;
        this.mServings = servings;
        this.mImageURL = imageURL;
    }

    public Recipe(Parcel in){
        mID = in.readInt();
        recipeName = in.readString();
        in.readTypedList(arrayOfIngredients, Ingredient.CREATOR);
        in.readTypedList(arrayOfSteps, Step.CREATOR);
        mServings = in.readInt();
        mImageURL = in.readString();

    }

    public final static Parcelable.Creator<Recipe> CREATOR =
            new Creator<Recipe>() {
                @Override
                public Recipe createFromParcel(Parcel parcel) {
                    return new Recipe(parcel);
                }

                @Override
                public Recipe[] newArray(int size) {
                    return new Recipe[size];
                }
            };

    public int getmID() {
        return mID;
    }

    public String getRecipeName() {
        return recipeName;
    }



    public ArrayList<Step> getArrayOfSteps() {
        return arrayOfSteps;
    }

    public int getmServings() {
        return mServings;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public ArrayList<Ingredient> getArrayOfIngredients(){return arrayOfIngredients; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(recipeName);
        dest.writeTypedList(arrayOfIngredients);
        dest.writeTypedList(arrayOfSteps);
        dest.writeInt(mServings);
        dest.writeString(mImageURL);
    }
}
