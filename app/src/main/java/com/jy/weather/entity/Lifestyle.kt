package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.StringUtil

/**
 * Lifestyle实体类
 *
 * Created by Yang on 2017/10/15.
 */
data class Lifestyle(
    val type: String,   //类型

    @SerializedName("brf")
    val brief: String,  //简要介绍

    @SerializedName("txt")
    val text: String    //详细信息
) {
    val title: String get() = StringUtil.getLiftStyleTitle(type)
    val resId: Int get() = DrawableUtil.getLifeStyleIconId(type)
}