package com.jy.weather.viewmodel

import android.databinding.ObservableField

class CitySearchItemViewModel(cityInfo: String) {

    val cityInfo: ObservableField<String> = ObservableField(cityInfo)
}