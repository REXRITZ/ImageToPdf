package com.ritesh.imagetopdf.model

import com.ritesh.imagetopdf.domain.utils.Utils

data class PdfOptions (
    var fileName: String = Utils.generateFileName(),
    var passwordEnabled: Boolean = false,
    var password: String = "",
    var pdfQuality: Quality = Quality.ORIGINAL,
    var orientation: Orientation = Orientation.AUTO,
)


