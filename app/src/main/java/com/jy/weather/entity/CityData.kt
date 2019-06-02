package com.jy.weather.entity

data class CityData(
    val city: String,
    val iconId: Int,
    val adminArea: String,
    val tempScope: String,
    var call: String,
    var phoneNumber: String
)