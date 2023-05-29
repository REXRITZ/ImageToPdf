package com.ritesh.imagetopdf.domain

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import com.ritesh.imagetopdf.domain.utils.Utils
import com.ritesh.imagetopdf.model.Dimen
import com.ritesh.imagetopdf.model.Orientation
import com.ritesh.imagetopdf.model.PdfOptions
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

class CreatePdf @Inject constructor(
    private val repository: PdfRepositoryImpl,
    private val contentResolver: ContentResolver
) {

    suspend operator fun invoke(pdfOptions: PdfOptions, images: List<Uri>) = flow {
        try {
            val quality = Utils.getQuality(pdfOptions.pdfQuality)
            val pageDimens = Utils.getPageSize(pdfOptions.orientation)
            val document = PDDocument()

            images.forEachIndexed { index, uri ->
                try {
                    createPdfPage(document, quality, pageDimens, uri, pdfOptions.orientation)
                    emit((index * 100/ images.size.toFloat()).toInt())
                } catch (e : Exception) {
                    emit(-1)
                }
            }
            if (pdfOptions.passwordEnabled) {
                val accessPermission = AccessPermission()
                val protectionPolicy = StandardProtectionPolicy(pdfOptions.password, pdfOptions.password, accessPermission).apply {
                    encryptionKeyLength = 128
                }
                document.protect(protectionPolicy)
            }

            val file = File(Utils.PATH, pdfOptions.fileName + ".pdf")
            document.save(file)
            document.close()

        repository.insert(PdfEntity(
        fileName = pdfOptions.fileName,
        filePath = file.absolutePath,
        dateCreated = Utils.getFormattedDate(LocalDate.now()),
        fileSize = Utils.convertFileSize(file.length()),
        isEncrypted = pdfOptions.passwordEnabled
        ))
        emit(100)

        } catch (e : Exception) {
            emit(-1)
        }
    }

    private suspend fun createPdfPage(
        document: PDDocument,
        quality: Float,
        pageDimens: Dimen,
        uri: Uri,
        orientation: Orientation
    ) {
        val imageDimen = getImageDimen(uri, pageDimens)
        val bitmap = getBitmap(uri, imageDimen)
        val image: PDImageXObject = JPEGFactory.createFromImage(document, bitmap, quality)
        var xOffset = 0f
        var yOffset = 0f
        val pageSize: PDRectangle =
            if(orientation == Orientation.AUTO) {
                PDRectangle(imageDimen.width, imageDimen.height)
            } else {
                xOffset = (pageDimens.width - imageDimen.width) / 2f
                yOffset = (pageDimens.height - imageDimen.height) / 2f
                PDRectangle(pageDimens.width, pageDimens.height)
            }
        val page = PDPage(pageSize)
        document.addPage(page)
        val contents = PDPageContentStream(document, page)
        contents.drawImage(image, xOffset, yOffset, imageDimen.width, imageDimen.height)
        contents.close()
        bitmap?.recycle()
    }

    private suspend fun getBitmap(uri: Uri, imageDimen: Dimen): Bitmap? = withContext(Dispatchers.IO){
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageDimen.width.toInt(), imageDimen.height.toInt(), false)
        inputStream?.close()
        scaledBitmap
    }

    private suspend fun getImageDimen(uri: Uri, pageDimen: Dimen): Dimen = withContext(Dispatchers.IO) {
        val inputStream = contentResolver.openInputStream(uri)
        val imageDimen = BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
            Utils.getScaledDimensions(outWidth, outHeight, pageDimen)
        }
        inputStream?.close()
        imageDimen
    }


}