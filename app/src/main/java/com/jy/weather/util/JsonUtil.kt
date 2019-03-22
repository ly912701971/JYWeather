package com.jy.weather.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.jy.weather.entity.Weather
import org.json.JSONObject

/**
 * 解析和处理JSON数据工具类
 *
 * Created by Yang on 2017/10/21.
 */

object JsonUtil {

    fun handleWeatherResponse(response: String?): Weather? {
        response ?: return null
        try {
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("HeWeather6")
            val weatherContent = jsonArray.getJSONObject(0).toString()
            return Gson().fromJson(weatherContent, Weather::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }
}
