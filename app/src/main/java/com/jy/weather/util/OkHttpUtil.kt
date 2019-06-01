package com.jy.weather.util

import okhttp3.*

/**
 * Http网络请求工具
 *
 * Created by Yang on 2017/10/14.
 */
object OkHttpUtil {

    private val client by lazy { OkHttpClient() }

    fun sendOkHttpRequest(url: String) = client.newCall(buildGetRequest(url)).execute()

    fun sendAsyncOkHttpRequest(url: String, callback: Callback) =
        client.newCall(buildGetRequest(url)).enqueue(callback)

    fun uploadJson(url: String, json: String) {
        val requestBody =
            FormBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        client.newCall(buildPostRequest(url, requestBody)).execute()
    }

    fun uploadJsonAsync(url: String, json: String, callback: Callback) {
        val requestBody =
            FormBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        client.newCall(buildPostRequest(url, requestBody)).enqueue(callback)
    }

    fun uploadLiveWeather(url: String, multipartBody: MultipartBody, callback: Callback) {
        client.newCall(buildPostRequest(url, multipartBody)).enqueue(callback)
    }

    private fun buildGetRequest(url: String) = Request.Builder().url(url).build()

    private fun buildPostRequest(url: String, requestBody: RequestBody) =
        Request.Builder().url(url).post(requestBody).build()
}