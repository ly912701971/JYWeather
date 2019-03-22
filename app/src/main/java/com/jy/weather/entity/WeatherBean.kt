package com.jy.weather.entity

import com.google.gson.annotations.SerializedName

/**
 * 和风天气实体类
 *
 * Created by Yang on 2017/10/15.
 */
data class WeatherBean(
    val status: String,

    val update: UpdateBean,

    val basic: BasicBean,

    val now: NowBean,

    @SerializedName("daily_forecast")
    val dailyForecasts: List<DailyForecastBean>,

    @SerializedName("hourly")
    val hourlyForecasts: List<HourlyForecastBean>,

    @SerializedName("lifestyle")
    val lifestyles: List<LifestyleBean>
)