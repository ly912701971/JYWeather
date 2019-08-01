package com.jy.weather.viewmodel

import androidx.databinding.ObservableField

class CitySearchItemViewModel(cityInfo: String) {

    val cityInfo: ObservableField<String> = ObservableField(cityInfo)
}