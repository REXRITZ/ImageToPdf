package com.ritesh.imagetopdf.data


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    private val dao: PdfDao
) {

    fun getAllPdfs(): Flow<List<PdfEntity>> {
        return dao.getAllPdfs()
    }

    suspend fun insert(pdfEntity: PdfEntity) {
        dao.insert(pdfEntity)
    }

    suspend fun getPdfById(id: Long): PdfEntity? {
        return dao.getPdfById(id)
    }

    suspend fun delete(pdfEntity: PdfEntity) {
        dao.delete(pdfEntity)
    }

    suspend fun delete(pdfEntityList: List<PdfEntity>) {
        dao.delete(pdfEntityList)
    }
}