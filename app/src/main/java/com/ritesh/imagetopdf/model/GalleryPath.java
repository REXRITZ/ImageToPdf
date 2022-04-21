package com.ritesh.imagetopdf.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GalleryPath implements Parcelable {

    public final ArrayList<Uri> galleryPhotos;

    protected GalleryPath(Parcel in) {
        galleryPhotos = in.createTypedArrayList(Uri.CREATOR);
    }

    public static final Creator<GalleryPath> CREATOR = new Creator<GalleryPath>() {
        @Override
        public GalleryPath createFromParcel(Parcel in) {
            return new GalleryPath(in);
        }

        @Override
        public GalleryPath[] newArray(int size) {
            return new GalleryPath[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(galleryPhotos);
    }
}
