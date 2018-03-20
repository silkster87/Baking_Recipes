package com.example.android.bakingrecipes.RecipeObjects;


import android.os.Parcel;
import android.os.Parcelable;

/**Each recipe has a series of ingredients to make it. Each ingredient has a specified quanity,
 * measure and name of the ingredient.
 * Created by Silky on 10/03/2018.
 */

public class Ingredient implements Parcelable{


    private int mQuantity;
    private String mMeasure;
    private String ingredient;

    public Ingredient(int mQuantity, String mMeasure, String ingredient) {
        this.mQuantity = mQuantity;
        this.mMeasure = mMeasure;
        this.ingredient = ingredient;
    }

    public Ingredient(Parcel in){
        mQuantity = in.readInt();
        mMeasure = in.readString();
        ingredient = in.readString();
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public String getmMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public final static Parcelable.Creator<Ingredient> CREATOR =
            new Creator<Ingredient>() {
                @Override
                public Ingredient createFromParcel(Parcel parcel) {
                    return new Ingredient(parcel);
                }

                @Override
                public Ingredient[] newArray(int size) {
                    return new Ingredient[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(ingredient);
    }
}
