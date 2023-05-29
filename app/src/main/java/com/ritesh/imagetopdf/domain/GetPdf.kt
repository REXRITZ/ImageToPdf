package com.ritesh.imagetopdf.domain

import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import javax.inject.Inject

class GetPdf @Inject constructor(
    private val repository : PdfRepositoryImpl
){

    suspend operator fun invoke(id: Long) : PdfEntity? {
        return repository.getPdfById(id)
    }
}