package com.jy.weather.navigator

import androidx.fragment.app.FragmentActivity

interface WeatherNavigator {
    fun getActivity(): FragmentActivity
    fun startTodayActivity()
    fun startCityManageActivity()
    fun startSettingActivity()
    fun startLiveWeatherActivity()
    fun showLifestyleDialog()
    fun startDataRefreshAnimator()
}