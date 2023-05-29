package com.ritesh.imagetopdf.domain.utils

import android.view.View

object Extensions {


    fun View.toggleVisibility(visible: Boolean) {
        visibility = if(visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}