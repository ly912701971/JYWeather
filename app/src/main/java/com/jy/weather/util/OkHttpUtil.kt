package com.jy.weather.util

import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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
        client.newCall(buildPostRequest(url, genRequestBody(json))).execute()
    }

    fun uploadJsonAsync(url: String, json: String, callback: Callback) {
        client.newCall(buildPostRequest(url, genRequestBody(json))).enqueue(callback)
    }

    fun uploadLiveWeather(url: String, multipartBody: MultipartBody, callback: Callback) {
        client.newCall(buildPostRequest(url, multipartBody)).enqueue(callback)
    }

    private fun genRequestBody(json: String) =
        json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    private fun buildGetRequest(url: String) = Request.Builder().url(url).build()

    private fun buildPostRequest(url: String, requestBody: RequestBody) =
        Request.Builder().url(url).post(requestBody).build()
}