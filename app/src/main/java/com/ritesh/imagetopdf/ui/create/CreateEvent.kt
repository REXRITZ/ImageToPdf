package com.ritesh.imagetopdf.ui.create

import android.net.Uri

sealed class CreateEvent {
    class OnSwapped(val oldPos: Int, val newPos: Int): CreateEvent()
    class OnDelete(val pos: Int) : CreateEvent()
    class OnInsert(val data: List<Uri>): CreateEvent()
    class UpdateUri(val uri: Uri) : CreateEvent()
    object CreatePdf : CreateEvent()
}
