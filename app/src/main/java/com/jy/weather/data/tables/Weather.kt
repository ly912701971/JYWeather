package com.jy.weather.data.tables

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class Weather(
    @Column(unique = true)
    val cityName: String,
    var weatherData: String,
    var call: String = "",
    var phoneNumber: String = ""
) : LitePalSupport()