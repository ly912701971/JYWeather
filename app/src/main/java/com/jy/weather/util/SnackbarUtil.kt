package com.jy.weather.util

import android.support.design.widget.Snackbar
import android.view.View

object SnackbarUtil {

    @JvmOverloads
    fun showSnackBar(
        view: View,
        text: String,
        action: String? = null,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        if (action != null && listener != null) {
            snackbar.setAction(action, listener)
        }
        snackbar.show()
    }
}

data class SnackbarObj(
    val text: String,
    val action: String? = null,
    val listener: View.OnClickListener? = null
)
