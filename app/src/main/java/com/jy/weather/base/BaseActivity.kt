package com.jy.weather.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import com.jy.weather.R
import com.jy.weather.databinding.ActivityBaseBinding

abstract class BaseActivity<Data> : AppCompatActivity(), IBaseActivity<Data> {

    private lateinit var binding: ActivityBaseBinding

    private lateinit var mToolbar: Toolbar
    private lateinit var mViewContainer: FrameLayout

    /**
     * 判断网络是否正常
     *
     * @return true:网络正常，false:网络异常
     */
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo != null && networkInfo.isNotEmpty()) {
                for (info in networkInfo) {
                    if (info.isConnected) {
                        return true
                    }
                }
            }
            return false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base)
        mToolbar = binding.toolbar
        mViewContainer = binding.viewContainer

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT// 强制竖屏
    }

    protected fun initToolbar(resId: Int, title: String) {
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationIcon(resId)
        mToolbar.title = title
    }

    protected fun setActivityView(resId: Int) {
        val view = LayoutInflater.from(this).inflate(resId, null)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mViewContainer.addView(view, lp)
    }

    /**
     * 设置状态栏颜色为目标颜色
     *
     * @param color 目标颜色
     */
    fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = color
        }
    }

    override fun showErrorMessage(msg: String) {
        Snackbar.make(mToolbar, msg, Snackbar.LENGTH_LONG).show()
    }
}
