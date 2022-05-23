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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectImageViewModel extends ViewModel {

    private final MutableLiveData<List<Album>> albumPhotosList = new MutableLiveData<>();
    private final MutableLiveData<List<ImageItem>> photosList = new MutableLiveData<>();
    private final MutableLiveData<String> currentAlbumObserver = new MutableLiveData<>();

    public LiveData<List<Album>> getAlbumPhotosList() {
        return albumPhotosList;
    }

    public LiveData<List<ImageItem>> getPhotosList() {
        return photosList;
    }

    public void setPhotosList(int position) {
        if (albumPhotosList.getValue() != null) {
            Album album = albumPhotosList.getValue().get(position);
            photosList.setValue(album.getAlbumImages());
            currentAlbumObserver.setValue(album.albumName);
        }
    }

    public LiveData<String> getCurrentAlbum() {
        return currentAlbumObserver;
    }

    public void loadData(Activity activity) {
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Select Camera"));
        albums.add(new Album("File Manager"));
        albums.add(new Album("All Images"));
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";


        Cursor cursor = activity.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            List<ImageItem> images = new ArrayList<>();
            Map<String, Album> folderMap = new HashMap<>();
            do {
                String idColumn = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                Uri contentUri = Uri.withAppendedPath(uri, idColumn);
                ImageItem image = new ImageItem(Integer.parseInt(idColumn), contentUri);
                images.add(image);
                if (folder != null) {
                    Album album = folderMap.get(folder);
                    if (album == null) {
                        album = new Album(folder);
                        folderMap.put(folder,album);
                    }
                    album.addImageToAlbum(image);
                }
            } while (cursor.moveToNext());
            cursor.close();
            albums.get(2).setAlbumImages(images);
            albums.addAll(folderMap.values());
        }
        albumPhotosList.postValue(albums);
        photosList.postValue(albums.get(2).getAlbumImages());
        currentAlbumObserver.postValue(albums.get(2).albumName);
    }
}
