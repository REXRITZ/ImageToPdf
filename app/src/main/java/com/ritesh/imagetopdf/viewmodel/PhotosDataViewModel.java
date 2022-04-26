package com.ritesh.imagetopdf.viewmodel;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritesh.imagetopdf.model.Album;
import com.ritesh.imagetopdf.model.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class PhotosDataViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> recentlyAdded = new MutableLiveData<>();
    private final MutableLiveData<List<Uri>> finalizedPhotos = new MutableLiveData<>();
    private final MutableLiveData<List<Album>> albumPhotosList = new MutableLiveData<>();

    public LiveData<List<Uri>> getRecentlyAddedPhotos() {
        return recentlyAdded;
    }

    public void addNewPhotos(List<Uri> newPhotos) {
        List<Uri> photos = new ArrayList<>();
        if (recentlyAdded.getValue() != null) {
            photos.addAll(recentlyAdded.getValue());
        }
        photos.addAll(newPhotos);
        recentlyAdded.postValue(photos);
    }

    public void removePhotos() {
        recentlyAdded.postValue(null);
    }

    public void setFinalizedPhotos(List<Uri> finalizedPhotos) {
        this.finalizedPhotos.postValue(finalizedPhotos);
    }

    public List<Uri> getFinalizedPhotos() {
        return this.finalizedPhotos.getValue();
    }

    public LiveData<List<Album>> getAlbumPhotosList() {
        return albumPhotosList;
    }

    public void loadData(Activity activity) {
//        if (albumPhotosList.getValue() != null) {
//            return;
//        }
        List<Album> albumList = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String sortOrder = MediaStore.Images.Media.DEFAULT_SORT_ORDER;


        Cursor cursor = activity.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder
        );

        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            List<String> folders = new ArrayList<>();
            folders.add("Select Camera");
            folders.add("File Manager");
            folders.add("All Images");
            albumList.add(new Album( "Select Camera", null));
            albumList.add(new Album("File Manager", null));
            albumList.add(new Album("All Images", null));
            List<ImageItem> allImages = new ArrayList<>();
            boolean flag = true;
            do {
                String idColumn = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                Uri contentUri = Uri.withAppendedPath(uri, idColumn);
                if (folders.contains(folder)) {
                    allImages.add(albumList.get(folders.indexOf(folder)).addImageToAlbum(contentUri));
                } else {
                    Album album = new Album(folder, contentUri);
                    allImages.add(album.addImageToAlbum(contentUri));
                    albumList.add(album);
                    folders.add(folder);
                }
                if (flag) {
                    albumList.get(2).setAlbumThumbnail(contentUri);
                }
                flag = false;
            } while (cursor.moveToNext());
            albumList.get(2).setAlbumImages(allImages);
            albumPhotosList.postValue(albumList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
