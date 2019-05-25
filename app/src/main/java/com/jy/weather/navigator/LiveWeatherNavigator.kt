package com.jy.weather.navigator

interface LiveWeatherNavigator {
    fun requestPermission()
    fun showLoginDialog()
    fun showLogoutDialog()
}