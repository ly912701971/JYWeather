package com.jy.weather.data.tables

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class Weather(
    @Column(unique = true)
    val cityId: String,
    var weatherData: String
) : LitePalSupport()