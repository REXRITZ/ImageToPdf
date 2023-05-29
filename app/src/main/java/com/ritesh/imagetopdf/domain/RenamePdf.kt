package com.ritesh.imagetopdf.domain

import com.ritesh.imagetopdf.data.PdfEntity
import com.ritesh.imagetopdf.data.PdfRepositoryImpl
import com.ritesh.imagetopdf.domain.utils.Utils
import javax.inject.Inject

class RenamePdf @Inject constructor(
    private val repository: PdfRepositoryImpl
) {

    suspend operator fun invoke(name: String, pdf: PdfEntity): String {
        if(FileHandler.renameFile(name, pdf.filePath)) {
            val newPath = Utils.PATH + "/$name.pdf"
            repository.insert(PdfEntity(
                id = pdf.id,
                fileName = name,
                filePath = newPath,
                dateCreated = pdf.dateCreated,
                fileSize = pdf.fileSize,
                isEncrypted = pdf.isEncrypted
            ))
            return "File renamed successfully"
        }
        return "Given file does not exist or has been deleted"
    }
}