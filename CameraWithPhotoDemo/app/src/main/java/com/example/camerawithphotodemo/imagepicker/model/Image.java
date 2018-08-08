package com.example.camerawithphotodemo.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    public long id;
    public String name;
    public String path;
    public String mineType;
    public long size;
    public int width;
    public int height;
    public boolean isSelected;

    public Image(long id, String name, String path, String mineType, long size, int width, int height, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.mineType = path;
        this.path = path;
        this.size = size;
        this.width = width;
        this.height = height;
        this.isSelected = isSelected;
    }

    private Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        mineType = in.readString();
        size = in.readLong();
        width = in.readInt();
        height = in.readInt();
        // isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(mineType);
        dest.writeLong(size);
        dest.writeInt(width);
        dest.writeInt(height);
        // dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };


}
