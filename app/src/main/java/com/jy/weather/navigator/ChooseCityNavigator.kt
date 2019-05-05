package com.jy.weather.navigator

interface ChooseCityNavigator {
    fun startOpenGpsActivity()
    fun startWeatherActivity(city: String)
}