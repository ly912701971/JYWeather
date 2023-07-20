package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil

/**
 * Created by liyang on 2023/7/19
 * email: liyang4@yy.com
 */
class HourlyData : WeatherDataWrapper() {
    @SerializedName("hourly")
    val data: List<Hourly>? = null
}

class Hourly {
    val cloud: String = ""
    val dew: String = ""
    val fxTime: String = ""
        get() = field.substring(field.indexOf("T") + 1, field.indexOf("+"))
    val humidity: String = ""
    val icon: String = ""
    val pop: String = ""
    val precip: String = ""
    val pressure: String = ""
    val temp: String = ""
        get() = "$field°"
    val text: String = ""
    val wind360: String = ""
    val windDir: String = ""
    val windScale: String = ""
        get() = if (StringUtil.hasNumber(field)) {
            "${field}级"
        } else {
            field
        }
    val windSpeed: String = ""
    val iconResId
        get() = DrawableUtil.getCondIcon(icon)
}