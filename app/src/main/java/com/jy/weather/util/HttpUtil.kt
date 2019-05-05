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

    private val client by lazy { OkHttpClient() }

    fun sendAsyncOkHttpRequest(url: String, callback: Callback) = client.newCall(buildRequest(url)).enqueue(callback)

    fun sendOkHttpRequest(url: String) = client.newCall(buildRequest(url)).execute()

    private fun buildRequest(url: String) = Request.Builder().url(url).build()
}