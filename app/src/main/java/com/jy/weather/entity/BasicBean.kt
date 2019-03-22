package com.jy.weather.entity

import com.google.gson.annotations.SerializedName

/**
 * Basic实体类
 *
 * Created by Yang on 2017/10/15.
 */
data class BasicBean(
    @SerializedName("cid")
    val cityId: String,      //城市id

    @SerializedName("location")
    val cityName: String,    //城市名称

    @SerializedName("parent_city")
    val parentCity: String,  //上级城市

    @SerializedName("admin_area")
    val adminArea: String,   //所属行政区

    @SerializedName("cnty")
    val country: String,     //国家

    @SerializedName("lon")
    val longitude: String,   //经度

    @SerializedName("lat")
    val latitude: String,    //纬度

    @SerializedName("tz")
    val timeZone: String    //时区
)
