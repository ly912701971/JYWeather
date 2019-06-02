package com.jy.weather.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.jy.weather.entity.LiveWeather
import com.jy.weather.entity.Weather
import org.json.JSONObject

/**
 * 解析和处理JSON数据工具类
 *
 * Created by Yang on 2017/10/21.
 */

object JsonUtil {
    private val gson: Gson by lazy { Gson() }

    fun handleWeatherResponse(response: String?): Weather? {
        response?.isEmpty() ?: return null
        try {
            return gson.fromJson(
                JSONObject(response).getJSONArray("HeWeather6").getJSONObject(0).toString(),
                Weather::class.java
            )
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    fun handleLiveWeatherResponse(response: String): List<LiveWeather> {
        val jsonArray = JsonParser().parse(response).asJsonArray
        val liveWeathers = mutableListOf<LiveWeather>()
        jsonArray.forEach {
            liveWeathers.add(gson.fromJson(it, LiveWeather::class.java))
        }
        return liveWeathers
    }
}
