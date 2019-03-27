package com.jy.weather.util

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Http网络请求工具
 *
 * Created by Yang on 2017/10/14.
 */
object HttpUtil {

    private val client = OkHttpClient()

    fun sendAsyncOkHttpRequest(address: String, callback: Callback) {
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }

    fun sendOkHttpRequest(address: String): Response {
        val request = Request.Builder().url(address).build()
        return client.newCall(request).execute()
    }
}