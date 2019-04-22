package com.jy.weather.navigator

interface ChooseCityNavigator {
    fun jumpToOpenGps()
    fun jumpToNewCity(city: String)
}