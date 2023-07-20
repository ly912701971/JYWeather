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

    private val weekdays =
        arrayOf("星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

    /**
     * 用于查看text是否含有数字
     */
    fun hasNumber(text: String) = Pattern.compile("[0-9]").matcher(text).find()

    /**
     * 返回紫外线强弱等级
     */
    fun getUvLevel(indexText: String) =
        when (Integer.parseInt(indexText)) {
            5 -> "很强"
            4 -> "强"
            3 -> "中等"
            2 -> "弱"
            else -> "最弱"
        }

    fun getIndexTitle(type: String) = when (type) {
        "1" -> "运动"
        "2" -> "洗车"
        "3" -> "穿衣"
        "5" -> "紫外线"
        "6" -> "旅游"
        "8" -> "舒适度"
        "9" -> "感冒"
        "10" -> "空气"
        else -> ""
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
}