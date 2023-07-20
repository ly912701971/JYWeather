package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil

/**
 * Created by liyang on 2023/7/20
 * email: liyang4@yy.com
 */
class IndexData : WeatherDataWrapper() {
    @SerializedName("daily")
    val data: List<Index>? = null
}

class Index {
    val category: String = ""
    val date: String = ""
    val level: String = ""
    val name: String = ""
    val text: String = ""
    val type: String = ""
    val title get() = StringUtil.getIndexTitle(type)
    val resId get() = DrawableUtil.getIndexIconId(type)
}