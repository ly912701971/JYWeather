package com.jy.weather.entity

/**
 * Created by liyang on 2023/7/19
 * email: liyang4@yy.com
 */
open class DataWrapper {
    val code: String = ""
    val refer: Refer? = null
}

open class WeatherDataWrapper : DataWrapper() {
    val fxLink: String = ""
    val updateTime: String = ""
}

class Refer {
    val license: List<String>? = null
    val sources: List<String>? = null
}