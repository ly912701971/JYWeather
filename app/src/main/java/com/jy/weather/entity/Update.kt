package com.jy.weather.entity

import com.google.gson.annotations.SerializedName

/**
 * Update实体类
 *
 * Created by Yang on 2017/12/11.
 */
data class Update(
    @SerializedName("loc")
    private val _loc: String,    //当地时间

    @SerializedName("utc")
    val utc: String             //UTC时间
) {

    val loc
        get() = _loc.split(" ".toRegex())[1]
}