package com.jy.weather.navigator

interface SettingNavigator {
    fun jumpToChooseCityActivity()
    fun openAutoUpdateService()
    fun showIntervalDialog()
    fun showClearCacheDialog()
}