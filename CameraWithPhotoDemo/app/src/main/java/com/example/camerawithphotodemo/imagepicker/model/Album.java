package com.example.camerawithphotodemo.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {

    public String name;
    public String cover;

    protected Album(Parcel in) {
        name = in.readString();
        cover = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(cover);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
