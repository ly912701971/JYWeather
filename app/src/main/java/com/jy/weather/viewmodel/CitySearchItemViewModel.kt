package com.jy.weather.viewmodel

import android.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.navigator.ChooseCityNavigator
import java.lang.ref.WeakReference
import java.util.*

class CitySearchItemViewModel(cityInfo: String) {

    val cityInfo: ObservableField<String> = ObservableField(cityInfo)

    private lateinit var navigator: WeakReference<ChooseCityNavigator>

    fun cityInfoClicked() {
        val citySet = HashSet(JYApplication.cityDB.citySet)
        var city = cityInfo.get() ?: return
        city = city.split(Regex(" - ")).getOrElse(0) { "unknown" }
        if (!citySet.contains(city)) {
            citySet.add(city)
            JYApplication.cityDB.citySet = citySet
            if (citySet.size == 1) {
                JYApplication.cityDB.defaultCity = city
            }
        }

        navigator.get()?.jumpToNewCity(city)
    }

    fun setNavigator(navigator: ChooseCityNavigator) {
        this.navigator = WeakReference(navigator)
    }
}