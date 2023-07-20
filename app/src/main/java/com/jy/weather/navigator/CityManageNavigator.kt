package com.jy.weather.navigator

import com.jy.weather.entity.Location

interface CityManageNavigator {
    fun startWeatherActivity(city: String, cityId: String, location: Location?)
}