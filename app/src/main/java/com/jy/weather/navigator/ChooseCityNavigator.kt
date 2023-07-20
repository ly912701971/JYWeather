package com.jy.weather.navigator

import androidx.fragment.app.FragmentActivity
import com.jy.weather.entity.Location

interface ChooseCityNavigator {
    fun getActivity(): FragmentActivity
    fun startOpenGpsActivity()
    fun startWeatherActivity(city: String?, cityId: String, location: Location? = null)
}