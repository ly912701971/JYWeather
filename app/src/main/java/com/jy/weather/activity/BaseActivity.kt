package com.jy.weather.activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jy.weather.R

/**
 * activity基类
 *
 * Created by Yang on 2017/10/22.
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarBackgroundDark()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//强制竖屏
    }

    /**
     * 设置状态栏颜色
     * 必须在setContentView()前调用
     */
    private fun setStatusBarColor(color: Int) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = color
    }

    /**
     * 设置状态栏透明
     */
    fun setStatusBarTrans() = setStatusBarColor(Color.TRANSPARENT)

    private fun setStatusBarBackgroundDark() =
        setStatusBarColor(getColorById(R.color.background_dark))

    private fun getColorById(resId: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(resId, null)
        } else {
            resources.getColor(resId)
        }

    fun getDialogParams(percentWidth: Double): LayoutParams {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return LayoutParams((metrics.widthPixels * percentWidth).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}