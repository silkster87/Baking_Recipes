package com.example.android.bakingrecipes.RecipeObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**A recipe will have a series of steps to carry out. Each step should contain an id,
 * short description and description. Not all steps will have a videoURL.
 * Created by Silky on 10/03/2018.
 */

public class Step implements Parcelable{


    private int mID;
    private String mShortDesc;
    private String mDescription;
    private String mVideoURL;
    private String mThumbNailURL;

    public Step(int id, String shortDescription, String description, String videoURL, String thumbNailURL){
        this.mID = id;
        this.mShortDesc = shortDescription;
        this.mDescription = description;
        this.mVideoURL = videoURL;
        this.mThumbNailURL = thumbNailURL;
    }

    public Step(Parcel in){
        mID = in.readInt();
        mShortDesc = in.readString();
        mDescription = in.readString();
        mVideoURL = in.readString();
        mThumbNailURL = in.readString();
    }

    public int getmID() {
        return mID;
    }

    public String getmShortDesc() {
        return mShortDesc;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmVideoURL() {
        return mVideoURL;
    }

    public String getmThumbNailURL(){return mThumbNailURL; }

    public final static Parcelable.Creator<Step> CREATOR =
            new Creator<Step>() {
                @Override
                public Step createFromParcel(Parcel parcel) {
                    return new Step(parcel);
                }

                @Override
                public Step[] newArray(int size) {
                    return new Step[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mShortDesc);
        dest.writeString(mDescription);
        dest.writeString(mVideoURL);
        dest.writeString(mThumbNailURL);
    }
}
