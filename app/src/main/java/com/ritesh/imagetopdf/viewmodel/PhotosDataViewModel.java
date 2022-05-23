package com.ritesh.imagetopdf.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotosDataViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> recentlyAdded = new MutableLiveData<>();
    private final MutableLiveData<List<Uri>> finalizedPhotos = new MutableLiveData<>();

    public LiveData<List<Uri>> getRecentlyAddedPhotos() {
        return recentlyAdded;
    }

    public List<Uri> getFinalizedPhotos() {
        return this.finalizedPhotos.getValue();
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
}
