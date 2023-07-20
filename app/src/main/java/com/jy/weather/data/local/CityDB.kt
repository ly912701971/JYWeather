package com.jy.weather.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.jy.weather.data.tables.Weather
import org.litepal.LitePal

/**
 * 城市数据库
 * 利用SharedPreferences、Litepal实现
 *
 * Created by Yang on 2018/1/9.
 */
class CityDB(context: Context) {

    companion object {
        private const val DB_BASE_PATH = "city_database"
        private const val DB_DEFAULT_CITY = "default_city"
        private const val DB_COND_CODE = "cond_code"
        private const val DB_LIVE_WEATHER_CACHE = "live_weather_cache"
    }

    private val sp: SharedPreferences =
        context.getSharedPreferences(DB_BASE_PATH, Context.MODE_PRIVATE)
    private val editor: Editor
        get() = sp.edit()

    var defaultCity: String?
        get() = sp.getString(DB_DEFAULT_CITY, null)
        set(defaultCity) {
            editor.putString(DB_DEFAULT_CITY, defaultCity).apply()
        }

    var condCode: String
        get() = sp.getString(DB_COND_CODE, null) ?: "0"
        set(condCode) {
            editor.putString(DB_COND_CODE, condCode).apply()
        }

    var liveWeatherCache: String
        get() = sp.getString(DB_LIVE_WEATHER_CACHE, null) ?: ""
        set(value) {
            editor.putString(DB_LIVE_WEATHER_CACHE, value).apply()
        }

    fun getCityDataFromDB(cityId: String) =
        LitePal.where("cityId = ?", cityId)
            .find(Weather::class.java)
            .getOrNull(0)?.weatherData ?: ""

    fun getAllCityDataFromDB(): MutableList<Weather> = LitePal.findAll(Weather::class.java)

    fun setCityDataToDB(
        cityId: String,
        weatherData: String
    ) {
        val weather = LitePal.where("cityId = ?", cityId)
            .find(Weather::class.java).getOrNull(0)
        if (weather == null) {
            Weather(cityId, weatherData).save()
        } else {
            weather.weatherData = weatherData
            weather.saveOrUpdate("cityId = ?", cityId)
        }
        LitePal.findAllAsync(Weather::class.java).listen {
            if (it.size == 1) {
                defaultCity = cityId
            }
        }
    }

    fun removeCityFromDB(cityId: String) =
        LitePal.deleteAll(Weather::class.java, "cityId = ?", cityId)

    fun clearCache() {
        LitePal.deleteAllAsync(Weather::class.java)
        editor.clear().apply()
    }
}
