package com.jy.weather.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.jy.weather.entity.DailyData
import com.jy.weather.entity.HourlyData
import com.jy.weather.entity.IndexData
import com.jy.weather.entity.LiveWeather
import com.jy.weather.entity.LocationData
import com.jy.weather.entity.NowData
import com.jy.weather.entity.WeatherData

/**
 * 解析和处理JSON数据工具类
 *
 * Created by Yang on 2017/10/21.
 */

object JsonUtil {

    val gson: Gson by lazy { Gson() }

    fun handleLiveWeatherResponse(response: String): List<LiveWeather> {
        val jsonArray = JsonParser().parse(response).asJsonArray
        val liveWeathers = mutableListOf<LiveWeather>()
        jsonArray.forEach {
            liveWeathers.add(gson.fromJson(it, LiveWeather::class.java))
        }
        return liveWeathers
    }

    private fun <T> handleData(rsp: String?, clazz: Class<T>): T? {
        rsp?.isEmpty() ?: return null
        try {
            return gson.fromJson(rsp, clazz)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    fun handleNowData(rsp: String?) = handleData(rsp, NowData::class.java)

    fun handleHourlyData(rsp: String?) = handleData(rsp, HourlyData::class.java)

    fun handleDailyData(rsp: String?) = handleData(rsp, DailyData::class.java)

    fun handleIndexData(rsp: String?) = handleData(rsp, IndexData::class.java)

    fun handleLocationData(rsp: String?) = handleData(rsp, LocationData::class.java)

    fun handleWeatherData(rsp: String?) = handleData(rsp, WeatherData::class.java)
}
