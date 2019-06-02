package com.jy.weather.navigator

interface WeatherNavigator {
    fun startTodayActivity()
    fun startCityManageActivity()
    fun startSettingActivity()
    fun startLiveWeatherActivity()
    fun startAutoUpdateService()
    fun startSendMessageService()
    fun showLifestyleDialog()
    fun startDataRefreshAnimator()
}