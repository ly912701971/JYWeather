package com.jy.weather.viewmodel

import android.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.navigator.ChooseCityNavigator
import java.lang.ref.WeakReference

class CitySearchItemViewModel(
    cityInfo: String,
    navigator: ChooseCityNavigator
) {

    val cityInfo: ObservableField<String> = ObservableField(cityInfo)

    private val navigator = WeakReference(navigator)

    fun cityInfoClicked() {
        var city = cityInfo.get() ?: return
        city = city.split(Regex(" - "))[0]
        JYApplication.cityDB.addCity(city)

        navigator.get()?.jumpToNewCity(city)
    }
}