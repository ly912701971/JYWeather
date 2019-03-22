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
data class NowBean(
    @SerializedName("cloud")
    val cloud: String,           //云量

    @SerializedName("cond_code")
    val code: String,            //天气状况代码

    @SerializedName("cond_txt")
    val condText: String,        //天气数据详情

    @SerializedName("fl")
    private val feelTemp: String,        //体感温度

    @SerializedName("hum")
    private val relativeHum: String,     //相对湿度

    @SerializedName("pcpn")
    val precipitation: String,   //降水量

    @SerializedName("pres")
    val airPressure: String,     //气压

    @SerializedName("tmp")
    private val temperature: String,     //温度

    @SerializedName("vis")
    private val visibility: String,      //能见度

    @SerializedName("wind_deg")
    val windDegree: String,      //风向(度数)

    @SerializedName("wind_dir")
    val windDirection: String,   //风向(方位)

    @SerializedName("wind_sc")
    private val windScale: String,       //风力等级

    @SerializedName("wind_spd")
    val windSpeed: String,       //风速

    private val nowIcon: Int,

    private val nowBackground: Int
) : Serializable {
    fun getFeelTemp(): String = "$feelTemp°"

    fun getRelativeHum(): String = "$relativeHum%"

    fun getTemperature(): String = "$temperature°"

    fun getVisibility(): String = "${visibility}km"

    fun getWindScale(): String =
        if (StringUtil.hasNumber(windScale)) {
            "${windScale}级"
        } else {
            windScale
        }

    fun getNowIcon(): Int = DrawableUtil.getCondIcon(code)

    fun getNowBackground(): Int = DrawableUtil.getBackground(code)
}
