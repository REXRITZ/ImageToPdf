package com.ritesh.imagetopdf.domain

import com.ritesh.imagetopdf.domain.utils.Utils
import java.io.File

object FileHandler {

    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return file.delete()
    }

    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

    fun renameFile(name: String, filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            file.renameTo(File(Utils.PATH, "$name.pdf"))
            return true
        }
        return false
    }

}