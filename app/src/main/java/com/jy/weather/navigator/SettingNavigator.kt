package com.jy.weather.navigator

interface SettingNavigator {
    fun startChooseCityActivity()
    fun startAutoUpdateService()
    fun showIntervalDialog()
    fun showClearCacheDialog()
}