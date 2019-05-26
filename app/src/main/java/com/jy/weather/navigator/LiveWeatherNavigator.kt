package com.jy.weather.navigator

interface LiveWeatherNavigator {
    fun requestPermission()
    fun showChooseImageDialog()
    fun showLoginHintDialog()
    fun showLoginDialog()
    fun showLogoutDialog()
}