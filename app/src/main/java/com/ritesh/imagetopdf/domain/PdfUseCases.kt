package com.ritesh.imagetopdf.domain

data class PdfUseCases(
    val deletePdf : DeletePdf,
    val getPdf : GetPdf,
    val createPdf: CreatePdf,
    val getAllPdfs: GetAllPdfs,
    val renamePdf: RenamePdf
)
