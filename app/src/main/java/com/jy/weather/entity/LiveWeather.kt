package com.jy.weather.entity

data class LiveWeather(
    val userName: String,
    val userPortrait: String,
    val liveTime: String,
    val liveText: String,
    val location: String,
    val liveUrl: String
)