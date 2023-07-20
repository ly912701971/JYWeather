package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil
import java.io.Serializable

/**
 * Created by liyang on 2023/7/20
 * email: liyang4@yy.com
 */
class DailyData : WeatherDataWrapper() {
    @SerializedName("daily")
    val data: List<Daily>? = null
}

class Daily : Serializable {
    val cloud: String = ""
    val fxDate: String = ""
    val humidity: String = ""
    val iconDay: String = ""
    val iconNight: String = ""
    val moonPhase: String = ""
    val moonPhaseIcon: String = ""
    val moonrise: String = ""
    val moonset: String = ""
    val precip: String = ""
    val pressure: String = ""
        get() = "${field}hPa"
    val sunrise: String = ""
    val sunset: String = ""
    val tempMax: String = ""
        get() = "$field°"
    val tempMin: String = ""
        get() = "$field°"
    val textDay: String = ""
    val textNight: String = ""
    val uvIndex: String = ""
        get() = StringUtil.getUvLevel(field)
    val vis: String = ""
    val wind360Day: String = ""
    val wind360Night: String = ""
    val windDirDay: String = ""
    val windDirNight: String = ""
    val windScaleDay: String = ""
    val windScaleNight: String = ""
    val windSpeedDay: String = ""
    val windSpeedNight: String = ""
    val date
        get() = fxDate.substring(fxDate.indexOf("-") + 1, fxDate.length)
    val weekday get() = StringUtil.getWeekday(fxDate)
    val icon get() = DrawableUtil.getCondIcon(iconDay)
    val tempScope get() = "$tempMin ~ ${tempMax}C"
}