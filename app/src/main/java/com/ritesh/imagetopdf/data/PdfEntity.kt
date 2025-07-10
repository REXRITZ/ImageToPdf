package com.ritesh.imagetopdf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf")
data class PdfEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long ?= null,
    val fileName: String,
    val filePath: String,
    val dateCreated: String,
    val fileSize: String,
    val isEncrypted: Boolean = false
)