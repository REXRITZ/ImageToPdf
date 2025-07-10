package com.ritesh.imagetopdf.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PdfEntity::class], version = 1, exportSchema = false)
abstract class PdfDatabase : RoomDatabase() {

    abstract val pdfDao: PdfDao

    companion object {
        const val DB_NAME = "pdf_db"
    }
}