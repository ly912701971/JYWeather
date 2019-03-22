package com.jy.weather.util

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Http网络请求工具
 *
 * Created by Yang on 2017/10/14.
 */
object HttpUtil {

    fun sendOkHttpRequest(address: String, callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }
}