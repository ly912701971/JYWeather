package com.jy.weather.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.entity.CityData

class CityListItemViewModel(data: CityData) {

    private var defaultCity = JYApplication.cityDB.defaultCity

    val cityName: ObservableField<String> = ObservableField(data.city)
    val iconId: ObservableField<Int> = ObservableField(data.iconId)
    val adminArea: ObservableField<String> = ObservableField(data.adminArea)
    val tempScope: ObservableField<String> = ObservableField(data.tempScope)
    val isDefaultCity: ObservableBoolean = ObservableBoolean(data.city == defaultCity)
}