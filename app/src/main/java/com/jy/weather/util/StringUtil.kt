package com.jy.weather.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * 字符串工具类
 *
 * Created by Yang on 2017/12/13.
 */
object StringUtil {

    private val weekdays = arrayOf("星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

    /**
     * 用于查看text是否含有数字
     */
    fun hasNumber(text: String) = Pattern.compile("[0-9]").matcher(text).find()

    /**
     * 返回紫外线强弱等级
     */
    fun getUvLevel(indexText: String): String {
        val index = Integer.parseInt(indexText)
        return when {
            index >= 11 -> "极强"
            index >= 8 -> "很强"
            index >= 6 -> "强"
            index >= 3 -> "中等"
            else -> "弱"
        }
    }

    fun getWeekday(dateText: String): String {
        var date: Date? = null
        try {
            date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(dateText)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return weekdays[Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_WEEK) - 1]
    }

    fun getTime(): String = SimpleDateFormat("HH:mm", Locale.CHINA).format(Date())

    fun getDateTime(): String = SimpleDateFormat("yy-MM-dd HH:mm", Locale.CHINA).format(Date())

    fun getMessage(call: String, weatherData: String): String {
        val endWord = "请留意天气变化，提前做好应对措施。\r\n[卷云天气]祝您生活愉快！"
        val builder = StringBuilder()
        if (call.contains(Regex("的"))) {
            builder.append("${call}，")
        } else {
            builder.append("亲爱的$call，")
        }
        val weather = JsonUtil.handleWeatherResponse(weatherData)
            ?: return builder.append(endWord).toString()
        val city = weather.basic.cityName
        val tempScope = "${weather.dailyForecasts[0].minTemp}~${weather.dailyForecasts[0].maxTemp}C"
        val condText = weather.now.condText
        val airBrief = weather.lifestyles.find { it.type == "air" }?.brief
        val uvBrief = weather.lifestyles.find { it.type == "uv" }?.brief
        return builder.append(
            "${city}今日天气为：${condText}，${tempScope}，空气质量${airBrief}，紫外线${uvBrief}。")
            .append(endWord).toString()
    }

    fun getNotification(weatherData: String): String {
        val builder = StringBuilder()
        val weather = JsonUtil.handleWeatherResponse(weatherData)
            ?: return builder.append("天气已更新，点击查看详情").toString()
        val tempScope = "${weather.dailyForecasts[0].minTemp}~${weather.dailyForecasts[0].maxTemp}C"
        val condText = weather.now.condText
        val airBrief = weather.lifestyles.find { it.type == "air" }?.brief
        val uvBrief = weather.lifestyles.find { it.type == "uv" }?.brief
        return builder.append("${condText}，${tempScope}，空气质量：${airBrief}，紫外线：${uvBrief}。")
            .toString()
    }
}