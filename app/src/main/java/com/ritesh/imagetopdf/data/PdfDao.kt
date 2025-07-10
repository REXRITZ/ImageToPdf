package com.ritesh.imagetopdf.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdfEntity: PdfEntity)

    @Delete
    suspend fun delete(pdfEntity: PdfEntity)

    @Delete
    suspend fun delete(pdfEntityList: List<PdfEntity>)

    @Query("SELECT * FROM pdf WHERE id = :id")
    suspend fun getPdfById(id: Long): PdfEntity?

    @Query("SELECT * FROM pdf ORDER BY id DESC")
    fun getAllPdfs(): Flow<List<PdfEntity>>

    @Query("UPDATE pdf SET fileName = :name WHERE id = :id")
    suspend fun renameFile(name: String, id: Long)
}