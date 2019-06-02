package com.jy.weather.navigator

interface CityManageNavigator {
    fun requestPermission(index: Int)
    fun startWeatherActivity(city: String)
}