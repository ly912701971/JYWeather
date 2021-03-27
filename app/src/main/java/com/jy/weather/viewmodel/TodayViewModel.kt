package com.jy.weather.viewmodel

import androidx.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.entity.DailyForecast
import com.jy.weather.entity.Now
import com.jy.weather.util.DrawableUtil

class TodayViewModel {

    private val db = JYApplication.cityDB

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val location: ObservableField<String> = ObservableField()
    val updateTime: ObservableField<String> = ObservableField()

    val icon: ObservableField<Int> = ObservableField()
    val temperature: ObservableField<String> = ObservableField()
    val feelTemp: ObservableField<String> = ObservableField()
    val relativeHum: ObservableField<String> = ObservableField()
    val windScale: ObservableField<String> = ObservableField()
    val windDirection: ObservableField<String> = ObservableField()
    val visibility: ObservableField<String> = ObservableField()

    val ultraviolet: ObservableField<String> = ObservableField()
    val airPressure: ObservableField<String> = ObservableField()
    val sunRise: ObservableField<String> = ObservableField()
    val sunSet: ObservableField<String> = ObservableField()
    val moonRise: ObservableField<String> = ObservableField()
    val moonSet: ObservableField<String> = ObservableField()

    fun start(
        city: String,
        updateTime: String,
        now: Now,
        dailyForecast: DailyForecast
    ) {
        this.location.set(city)
        this.updateTime.set(updateTime)

        this.apply {
            icon.set(now.nowIconId)
            temperature.set(now.temperature)
            feelTemp.set(now.feelTemp)
            relativeHum.set(now.relativeHum)
            windScale.set(now.windScale)
            windDirection.set(now.windDirection)
            visibility.set(now.visibility)

            ultraviolet.set(dailyForecast.ultraviolet)
            airPressure.set(dailyForecast.airPressure)
            sunRise.set(dailyForecast.sunRise)
            sunSet.set(dailyForecast.sunSet)
            moonRise.set(dailyForecast.moonRise)
            moonSet.set(dailyForecast.moonSet)
        }
    }
}