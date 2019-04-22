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
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = JYApplication.INSTANCE
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
}