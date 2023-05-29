package com.ritesh.imagetopdf.domain

import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllPdfs @Inject constructor(
    private val repository: PdfRepositoryImpl
) {

    operator fun invoke(): Flow<List<PdfEntity>> {
        return repository.getAllPdfs().map { pdfEntityList ->
            val (synced, toBeDeleted) = pdfEntityList.partition { FileHandler.fileExists(it.filePath) }
            if(toBeDeleted.isNotEmpty()) {
                repository.delete(toBeDeleted)
            }
            synced
        }
    }
}