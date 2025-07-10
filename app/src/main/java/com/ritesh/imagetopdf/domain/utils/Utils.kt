package com.ritesh.imagetopdf.domain.utils

import android.os.Environment
import com.ritesh.imagetopdf.BuildConfig
import com.ritesh.imagetopdf.model.Dimen
import com.ritesh.imagetopdf.model.Orientation
import com.ritesh.imagetopdf.model.Quality
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Utils {
    val PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
    const val APPID = BuildConfig.APPLICATION_ID
    const val AUTHORITY = "$APPID.fileprovider"
    const val VERSION = BuildConfig.VERSION_NAME
    const val STORE_LINK = "https://play.google.com/store/apps/details?id=$APPID"
    const val DAYS_FOR_FLEXIBLE_UPDATE = 2
    const val APP_UPDATE_CODE = 109

    fun getFormattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return date.format(formatter)
    }

    fun convertFileSize(fileSize: Long): String {
        val name: String
        var size = fileSize.toFloat()
        if (fileSize < 1000) {
            name = "B"
        } else if (fileSize < 1000000) {
            name = "kB"
            size /= 1000f
        } else if (fileSize < 1000000000) {
            name = "MB"
            size /= 1000000f
        } else {
            name = "GB"
            size /= 1000000000f
        }
        return String.format(Locale.getDefault(), "%.1f %s", size, name)
    }

    fun getScaledDimensions(w: Int, h: Int, pageDimen: Dimen): Dimen {
        val ratio = (pageDimen.height / h.toFloat()).coerceAtMost(pageDimen.width / w.toFloat())
        return Dimen((ratio * w).toInt().toFloat(), (ratio * h).toInt().toFloat())
    }

    fun generateFileName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyymmddHHmmss")
        return "image_to_pdf${LocalDateTime.now().format(formatter)}"
    }

    fun generateImageName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyymmddHHmmss")
        return "image_${LocalDateTime.now().format(formatter)}"
    }

    fun getQuality(quality: Quality): Float {
        return when(quality) {
            Quality.LOW -> 0.25f
            Quality.MEDIUM -> 0.6f
            Quality.HIGH -> 0.75f
            Quality.ORIGINAL -> 1f
        }
    }

    fun getPageSize(orientation: Orientation): Dimen {
        if (orientation == Orientation.LANDSCAPE) {
            return Dimen(842f, 595f)
        }
        return Dimen(595f, 842f)
    }

    fun isFileNameValid(name: String): Boolean {
        val pattern = "/\\:*?\"<>|"
        for (i in 0 until name.length) {
            for (j in 0 until pattern.length) {
                if (name[i] == pattern[j]) {
                    return false
                }
            }
        }
        return true
    }
}