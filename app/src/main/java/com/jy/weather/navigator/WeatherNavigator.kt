package com.jy.weather.navigator

interface WeatherNavigator {
    fun jumpToTodayActivity()
    fun jumpToCityManageActivity()
    fun jumpToSettingActivity()
    fun startAutoUpdateService()
    fun showLifestyleDialog()
    fun onDataRefresh()
}