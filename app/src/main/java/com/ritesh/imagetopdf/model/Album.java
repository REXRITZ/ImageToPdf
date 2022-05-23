package com.ritesh.imagetopdf.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Album {

    public final String albumName;
    private List<ImageItem> albumImages;

    public Album(String albumName) {
        this.albumName = albumName;
        albumImages = new ArrayList<>();
    }

    public Uri getAlbumThumbnail() {
        return albumImages.get(0).imageUri;
    }

    public List<ImageItem> getAlbumImages() {
        return albumImages;
    }

    public void setAlbumImages(List<ImageItem> albumImages) {
        this.albumImages = albumImages;
    }

    public void addImageToAlbum(ImageItem imageItem) {
        albumImages.add(imageItem);
    }

    public int getItemCount() {
        return albumImages.size();
    }
}
