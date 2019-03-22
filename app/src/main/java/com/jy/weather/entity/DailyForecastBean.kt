package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil
import java.io.Serializable

/**
 * 天气预报实体类
 *
 * Created by Yang on 2017/10/15.
 */
data class DailyForecastBean(
    private val date: String,   //日期

    @SerializedName("hum")
    val relativuHum: String,    //相对湿度

    @SerializedName("pcpn")
    val precipitation: String,  //降水量

    @SerializedName("pop")
    val probability: String,    //降水概率

    @SerializedName("pres")
    private val airPressure: String,    //气压

    @SerializedName("uv_index")
    private val ultraviolet: String,    //紫外线

    @SerializedName("vis")
    val visibility: String,     //能见度

    @SerializedName("cond_code_d")
    val dayCondCode: String,    //白天天气状况代码

    @SerializedName("cond_code_n")
    val nightCondCode: String,  //晚上天气状况代码

    @SerializedName("cond_txt_d")
    val dayCondText: String,    //白天天气状况描述

    @SerializedName("cond_txt_n")
    val nightCondText: String,  //晚上天气状况描述

    @SerializedName("sr")
    val sunRise: String,    //日升时间

    @SerializedName("ss")
    val sunSet: String,     //日落时间

    @SerializedName("mr")
    val moonRise: String,   //月升时间

    @SerializedName("ms")
    val moonSet: String,    //月落时间

    @SerializedName("tmp_max")
    private val maxTemp: String,    //最大温度

    @SerializedName("tmp_min")
    private val minTemp: String,    //最低温度

    @SerializedName("wind_deg")
    val windDegree: String,     //风向(度数)

    @SerializedName("wind_dir")
    val windDirection: String,  //风向(方位)

    @SerializedName("wind_sc")
    val windScale: String,      //风力等级

    @SerializedName("wind_spd")
    val windSpeed: String,      //风速

    private val weekday: String,

    private val icon: Int
) : Serializable {
    fun getDate(): String = date.substring(date.indexOf("-") + 1, date.length)

    fun getAirPressure(): String = "${airPressure}hPa"

    fun getUltraviolet(): String = StringUtil.getUvLevel(ultraviolet)

    fun getMaxTemp(): String = "$maxTemp°"

    fun getMinTemp(): String = "$minTemp°"

    fun getWeekday(): String = StringUtil.getWeekday(date)

    fun getIcon(): Int = DrawableUtil.getCondIcon(dayCondCode)
}
