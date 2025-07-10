package com.ritesh.imagetopdf.domain

import android.util.Log
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import javax.inject.Inject


class DeletePdf @Inject constructor(
    private val repository: PdfRepositoryImpl
){

    suspend operator fun invoke(pdfEntity: PdfEntity) {
        FileHandler.deleteFile(pdfEntity.filePath)
        repository.delete(pdfEntity)
    }
}