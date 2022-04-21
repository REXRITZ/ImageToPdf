package com.ritesh.imagetopdf.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PhotosDataViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Uri>> recentlyAdded = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Uri>> finalizedPhotos = new MutableLiveData<>();

    public LiveData<ArrayList<Uri>> getRecentlyAddedPhotos() {
        return recentlyAdded;
    }

    public void addNewPhotos(ArrayList<Uri> newPhotos) {
        recentlyAdded.postValue(newPhotos);
    }

    public void removePhotos() {
        recentlyAdded.postValue(null);
    }

    public void setFinalizedPhotos(ArrayList<Uri> finalizedPhotos) {
        this.finalizedPhotos.postValue(finalizedPhotos);
    }

    public ArrayList<Uri> getFinalizedPhotos() {
        return this.finalizedPhotos.getValue();
    }

}
