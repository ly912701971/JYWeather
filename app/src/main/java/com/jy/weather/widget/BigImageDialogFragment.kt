package com.jy.weather.widget

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jy.weather.R

class BigImageDialogFragment : DialogFragment() {

    private var imageView: ImageView? = null
    private var imageUrl: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setOnDismissListener {
                imageView?.setImageDrawable(null)
            }
            window?.apply {
                setWindowAnimations(R.style.dialog_animation)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_big_image, container, false)
        imageView = view.findViewById(R.id.iv_big_image)
        imageView?.setOnClickListener { dismiss() }
        setImageUrl(imageUrl)
        return view
    }

    fun setImageUrl(url: String) {
        imageUrl = url
        if (imageView != null) {
            Glide.with(imageView!!).load(imageUrl).into(imageView!!)
        }
    }
}