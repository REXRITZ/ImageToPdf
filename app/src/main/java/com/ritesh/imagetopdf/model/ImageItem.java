package com.ritesh.imagetopdf.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ImageItem {

    public final int id;
    public final Uri imageUri;
    public boolean isSelected;

    public ImageItem(int id, Uri imageUri) {
        this.imageUri = imageUri;
        isSelected = false;
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ImageItem)) {
            return false;
        }
        ImageItem imageItem = (ImageItem) obj;
        return Objects.equals(this.imageUri, imageItem.imageUri)
                && Objects.equals(this.isSelected, imageItem.isSelected)
                && Objects.equals(this.id, imageItem.id);
    }
}
