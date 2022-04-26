package com.ritesh.imagetopdf.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Album {

    public final String albumName;
    public Uri albumThumbnail;
    private List<ImageItem> albumImages;
    private Integer itemCount;

    public Album(String albumName, Uri albumThumbnail) {
        this.albumName = albumName;
        this.albumThumbnail = albumThumbnail;
        albumImages = new ArrayList<>();
        itemCount = 0;
    }

    public void setAlbumThumbnail(Uri uri) {
        this.albumThumbnail = uri;
    }

    public List<ImageItem> getAlbumImages() {
        return albumImages;
    }

    public void setAlbumImages(List<ImageItem> albumImages) {
        this.albumImages = albumImages;
        itemCount = albumImages.size();
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public ImageItem addImageToAlbum(Uri uri) {
        final ImageItem imageItem = new ImageItem(uri);
        albumImages.add(imageItem);
        itemCount = albumImages.size();
        return imageItem;
    }
}
