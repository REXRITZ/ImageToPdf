package com.ritesh.imagetopdf.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ImageItem {

    public final Uri imageUri;
    public boolean isSelected;

    public ImageItem(Uri imageUri) {
        this.imageUri = imageUri;
        isSelected = false;
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
                && Objects.equals(this.isSelected, imageItem.isSelected);
    }
}
