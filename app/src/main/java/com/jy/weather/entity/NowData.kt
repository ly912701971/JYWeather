package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil
import java.io.Serializable

/**
 * Now实体类
 *
 * Created by Yang on 2017/10/15.
 */
class NowData : WeatherDataWrapper() {
    @SerializedName("now")
    val data: Now? = null
}

class Now : Serializable {
    val cloud: String = ""
    val dew: String = ""
    val feelsLike: String = ""
    val humidity: String = ""
    val icon: String = ""
    val obsTime: String = ""
        get() = "发布时间：" + field.substring(field.indexOf("-") + 1, field.indexOf("+"))
            .replace("T", " ")
    val precip: String = ""
    val pressure: String = ""
    val temp: String = ""
        get() = "$field°"
    val text: String = ""
    val vis: String = ""
        get() = "${field}km"
    val wind360: String = ""
    val windDir: String = ""
    val windScale: String = ""
        get() = if (StringUtil.hasNumber(field)) {
            "${field}级"
        } else {
            field
        }
    val windSpeed: String = ""
    val iconId get() = DrawableUtil.getCondIcon(icon)
    val bdId get() = DrawableUtil.getBackground(icon)
}
