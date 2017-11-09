package com.example.hanks.photoviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * PictureData
 * Created by hanks on 17-11-8.
 */

public class PictureData implements Parcelable {
    public int[] location;
    public int[] size;
    public int[] imageSize;
    public String url;
    public float[] matrixValue;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.location);
        dest.writeIntArray(this.size);
        dest.writeIntArray(this.imageSize);
        dest.writeString(this.url);
        dest.writeFloatArray(this.matrixValue);
    }

    public PictureData() {
    }

    protected PictureData(Parcel in) {
        this.location = in.createIntArray();
        this.size = in.createIntArray();
        this.imageSize = in.createIntArray();
        this.url = in.readString();
        this.matrixValue = in.createFloatArray();
    }

    public static final Creator<PictureData> CREATOR = new Creator<PictureData>() {
        @Override
        public PictureData createFromParcel(Parcel source) {
            return new PictureData(source);
        }

        @Override
        public PictureData[] newArray(int size) {
            return new PictureData[size];
        }
    };
}
