package com.ritesh.imagetopdf.ui.home.options

import androidx.lifecycle.*
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.domain.PdfUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfOptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PdfUseCases
) : ViewModel() {


    private val _pdfData = MutableLiveData<PdfEntity>()
    val pdfData: LiveData<PdfEntity> = _pdfData

    val showMessage = MutableLiveData("")

    init {
        savedStateHandle.get<Long>("pdfId")?.let {
            viewModelScope.launch {
                val result = useCases.getPdf(it)
                _pdfData.postValue(result!!)
            }
        }
    }

    fun deleteFile() {
        val pdfEntity = pdfData.value!!
        viewModelScope.launch(Dispatchers.IO) {
            useCases.deletePdf(pdfEntity)
        }
    }

    fun renameFile(name: String) {
        val pdf = pdfData.value!!
        viewModelScope.launch {
            val result = useCases.renamePdf(name, pdf)
            showMessage.postValue(result)
        }
    }
}
