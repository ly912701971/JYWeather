package com.jy.weather.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.entity.WeatherData

class CityListItemViewModel(data: WeatherData) {

    private var defaultCity = JYApplication.cityDB.defaultCity

    val cityName: ObservableField<String> = ObservableField(data.city)
    val iconId: ObservableField<Int> = ObservableField(data.now?.iconId)
    val adminArea: ObservableField<String> = ObservableField(data.location?.loc)
    val tempScope: ObservableField<String> = ObservableField(data.daily?.getOrNull(0)?.tempScope)
    val isDefaultCity: ObservableBoolean = ObservableBoolean(data.cityId == defaultCity)
}