package com.jy.weather.network

import com.jy.weather.util.HttpUtil
import okhttp3.Callback

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private val BASE = "https://free-api.heweather.com/s6/weather"

    private val NOW = "/now"

    private val FORECAST = "/forecast"

    private val HOURLY = "/hourly"

    private val LIFESTYLE = "/lifestyle"

    private val LOCATION = "?location="

    private val KEY = "&key=4d9d9383c876415a92bb9e2fddba0b15"

    fun queryWeatherData(city: String, callback: Callback) {
        HttpUtil.sendOkHttpRequest(BASE + LOCATION + city + KEY, callback)
    }

    fun queryNowData(city: String, callback: Callback) {
        HttpUtil.sendOkHttpRequest(BASE + NOW + LOCATION + city + KEY, callback)
    }

    fun queryForecastData(city: String, callback: Callback) {
        HttpUtil.sendOkHttpRequest(BASE + FORECAST + LOCATION + city + KEY, callback)
    }

    fun queryHourlyData(city: String, callback: Callback) {
        HttpUtil.sendOkHttpRequest(BASE + HOURLY + LOCATION + city + KEY, callback)
    }

    fun queryLifestyleData(city: String, callback: Callback) {
        HttpUtil.sendOkHttpRequest(BASE + LIFESTYLE + LOCATION + city + KEY, callback)
    }
}
