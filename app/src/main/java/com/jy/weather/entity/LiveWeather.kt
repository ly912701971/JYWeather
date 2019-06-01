package com.jy.weather.entity

data class LiveWeather(
    val liveId: Int,
    val userName: String,
    val userPortrait: String,
    val liveTime: String,
    val liveText: String,
    val location: String,
    val liveUrl: String,
    var likeNum: Int,
    var hasLiked: Int,
    val commentArray: MutableList<Comment>
)