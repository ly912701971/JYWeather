package com.jy.weather.viewmodel

import androidx.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.entity.Daily
import com.jy.weather.entity.Now
import com.jy.weather.util.DrawableUtil

class TodayViewModel {

    val bgResId: ObservableField<Int> =
        ObservableField(DrawableUtil.getBackground(JYApplication.cityDB.condCode))
    val location: ObservableField<String> = ObservableField()
    val now: ObservableField<Now> = ObservableField()
    val daily: ObservableField<Daily> = ObservableField()

    fun start(city: String, now: Now, daily: Daily) {
        this.location.set(city)
        this.now.set(now)
        this.daily.set(daily)
    }
}