package com.jy.weather.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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
}