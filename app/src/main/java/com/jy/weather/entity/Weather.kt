package com.jy.weather.entity

import com.google.gson.annotations.SerializedName

/**
 * 和风天气实体类
 *
 * Created by Yang on 2017/10/15.
 */
data class Weather(
    val status: String,

    val update: Update,

    val basic: Basic,

    val now: Now,

    @SerializedName("daily_forecast")
    val dailyForecasts: List<DailyForecast>,

    @SerializedName("hourly")
    val hourlyForecasts: List<HourlyForecast>,

    @SerializedName("lifestyle")
    val lifestyles: List<Lifestyle>
)