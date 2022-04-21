package com.ritesh.imagetopdf.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {

    public final Uri thumbnailPath;
    public final String filePath;

    public Result(Uri thumbnailPath, String filePath) {
        this.thumbnailPath = thumbnailPath;
        this.filePath = filePath;
    }

    protected Result(Parcel in) {
        thumbnailPath = in.readParcelable(Uri.class.getClassLoader());
        filePath = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(thumbnailPath, i);
        parcel.writeString(filePath);
    }
}
