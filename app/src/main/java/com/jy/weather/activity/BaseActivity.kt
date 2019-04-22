package com.jy.weather.activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import com.jy.weather.R

/**
 * activity基类
 *
 * Created by Yang on 2017/10/22.
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//强制竖屏
    }

    /**
     * 设置状态栏透明
     * 必须在setContentView()前调用
     */
    fun setStatusBarTrans() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 设置状态栏为背景色
     * 必须在setContentView()前调用
     */
    fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = getColorById(R.color.background_dark)
        }
    }

    fun getColorById(resId: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(resId, null)
        } else {
            resources.getColor(resId)
        }

    fun getDialogParams(percentWidth: Double): LayoutParams {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return LayoutParams((metrics.widthPixels * percentWidth).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}