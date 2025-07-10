package com.ritesh.imagetopdf.ui.create

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.ritesh.imagetopdf.domain.PdfUseCases
import com.ritesh.imagetopdf.model.PdfOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class CreatePdfViewModel @Inject constructor(
    private val useCases: PdfUseCases,
) : ViewModel() {

    private val images = mutableListOf<Uri>()

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    val pdfOptions: PdfOptions = PdfOptions()

    var cropUriPos: Int = -1

    fun onEvent(event: CreateEvent) {
        when(event) {
            is CreateEvent.OnDelete -> {
                images.removeAt(event.pos)
                _uiState.value = UiState.OnListUpdate(images)
            }
            is CreateEvent.OnInsert -> {
                images.addAll(event.data)
                _uiState.value = UiState.OnListUpdate(images)
            }
            is CreateEvent.OnSwapped -> {
                Collections.swap(images, event.oldPos, event.newPos)
                _uiState.value = UiState.OnListUpdate(images)
            }
            CreateEvent.CreatePdf -> {
                createPdf()
            }
            is CreateEvent.UpdateUri -> {
                if(cropUriPos != -1) {
                    images[cropUriPos] = event.uri
                    cropUriPos = -1
                    _uiState.value = UiState.OnListUpdate(images)
                }
            }
        }
    }

    private fun createPdf() {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.createPdf(pdfOptions,images)
                .collect {
                    when(it) {
                        -1 -> {
                            //Some error occurred
                            _uiState.postValue(UiState.Error("Error occurred while creating pdf."))
                            cancel()
                        }
                        else  -> {
                            _uiState.postValue(UiState.Progress(it))
                        }
                    }
                }
        }
    }

    fun getThumbnail(): Uri {
        return images[0]
    }

    fun logEvent(firebaseAnalytics: FirebaseAnalytics) {
        firebaseAnalytics.logEvent(
            "pdf_created",
            Bundle().apply {
                putBoolean("password_enabled", pdfOptions.passwordEnabled)
                putInt("total_images", images.size)
                putString("quality", pdfOptions.pdfQuality.name)
                putString("orientation", pdfOptions.orientation.name)
            }
        )
    }

    sealed class UiState {
        data class OnListUpdate(val data: List<Uri>): UiState()
        data class Progress(val value: Int): UiState()
        data class Error(val message: String): UiState()
    }
}