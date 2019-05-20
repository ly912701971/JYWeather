package com.jy.weather.data.remote

import com.jy.weather.JYApplication
import com.jy.weather.util.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private const val KEY = "&key=4d9d9383c876415a92bb9e2fddba0b15"
    private const val BASE = "https://free-api.heweather.com/s6/weather"
    private const val LOCATION = "?location="

    @JvmOverloads
    fun queryWeatherDataAsync(
        city: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        HttpUtil.sendAsyncOkHttpRequest("$BASE$LOCATION$city$KEY", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = (response.body() ?: return).string()
                JYApplication.cityDB.setCityDataToDB(city, data)
                onSuccess(data)
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }
        })
    }
}
