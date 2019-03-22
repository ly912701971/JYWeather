package com.jy.weather.entity

import com.google.gson.annotations.SerializedName

/**
 * Update实体类
 *
 * Created by Yang on 2017/12/11.
 */
data class UpdateBean(
    @SerializedName("loc")
    private val loc: String,    //当地时间

    @SerializedName("utc")
    val utc: String             //UTC时间
) {
    fun getLoc(): String = loc.split(" ".toRegex()).getOrElse(1) { "unknown" }
}