package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil

/**
 * 未来每小时预报
 *
 * Created by Yang on 2017/10/15.
 */
data class HourlyForecast(
    @SerializedName("time")
    private val _time: String,   //时间

    @SerializedName("hum")
    val relativeHum: String,    //相对湿度

    @SerializedName("pop")
    val probability: String,    //降水概率

    @SerializedName("dew")
    val dewTemp: String,        //露点温度

    @SerializedName("pres")
    val airPressure: String,    //气压

    @SerializedName("tmp")
    private val _temperature: String,    //温度

    @SerializedName("wind_deg")
    val windDegree: String,     //风向(度数)

    @SerializedName("wind_dir")
    val windDirection: String,  //风向(方位)

    @SerializedName("wind_sc")
    private val _windScale: String,  //风力等级

    @SerializedName("wind_spd")
    val windSpeed: String,  //风速

    @SerializedName("cloud")
    val cloud: String,      //云量

    @SerializedName("cond_code")
    val code: String,       //天气状况代码

    @SerializedName("cond_txt")
    val condText: String    //天气数据详情
) {

    val time
        get() = _time.split(" ".toRegex())[1]

    val temperature
        get() = "$_temperature°"

    val windScale
        get() = if (StringUtil.hasNumber(_windScale)) {
            "${_windScale}级"
        } else {
            _windScale
        }

    val icon
        get() = DrawableUtil.getCondIcon(code)
}
