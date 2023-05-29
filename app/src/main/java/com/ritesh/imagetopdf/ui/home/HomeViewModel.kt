package com.ritesh.imagetopdf.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.domain.PdfUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: PdfUseCases
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    init {
        getAllPdfs()
    }

    private fun getAllPdfs() {
        _uiState.value = UiState.ProgressShow
        viewModelScope.launch(Dispatchers.IO){
            useCases.getAllPdfs().collect {
                _uiState.postValue(UiState.Success(it))
            }
        }
    }

    fun delete(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO){
            useCases.deletePdf(pdfEntity)
        }
    }

}

sealed class UiState {
    data class Success(val data: List<PdfEntity>): UiState()
    object ProgressShow: UiState()
}