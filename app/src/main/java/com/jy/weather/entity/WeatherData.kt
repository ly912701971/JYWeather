package com.jy.weather.entity

/**
 * Created by liyang on 2023/7/20
 * email: liyang4@yy.com
 */
class WeatherData {
    var city: String = ""
    var cityId: String = ""
    var now: Now? = null
    var hourly: List<Hourly>? = null
    var daily: List<Daily>? = null
    var index: List<Index>? = null
    var location: Location? = null
}