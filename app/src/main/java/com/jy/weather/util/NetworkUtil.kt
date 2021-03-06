package com.jy.weather.util

import android.content.Context
import android.net.ConnectivityManager
import com.jy.weather.JYApplication

object NetworkUtil {

    /**
     * 判断网络是否正常
     *
     * @return true:网络正常，false:网络异常
     */
    fun isNetworkAvailable() =
        (JYApplication.INSTANCE
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .allNetworkInfo.find { it.isConnected } != null
}
