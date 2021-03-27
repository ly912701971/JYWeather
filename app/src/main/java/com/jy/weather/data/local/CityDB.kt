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
        private const val DB_NOTIFICATION = "notification"
        private const val DB_SMART_REMIND = "smart_remind"
        private const val DB_AUTO_UPDATE = "auto_update"
        private const val DB_UPDATE_INTERVAL = "update_interval"
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

    // 默认不打开通知栏
    var notification: Boolean
        get() = sp.getBoolean(DB_NOTIFICATION, false)
        set(notification) {
            editor.putBoolean(DB_NOTIFICATION, notification).apply()
        }

    var smartRemind: Boolean
        get() = sp.getBoolean(DB_SMART_REMIND, false)
        set(value) {
            editor.putBoolean(DB_SMART_REMIND, value).apply()
        }

    // 默认不自动更新
    var autoUpdate: Boolean
        get() = sp.getBoolean(DB_AUTO_UPDATE, false)
        set(autoUpdate) {
            editor.putBoolean(DB_AUTO_UPDATE, autoUpdate).apply()
        }

    // 默认更新时间为2小时
    var updateInterval: Int
        get() = sp.getInt(DB_UPDATE_INTERVAL, 2)
        set(hour) {
            editor.putInt(DB_UPDATE_INTERVAL, hour).apply()
        }

    var liveWeatherCache: String
        get() = sp.getString(DB_LIVE_WEATHER_CACHE, null) ?: ""
        set(value) {
            editor.putString(DB_LIVE_WEATHER_CACHE, value).apply()
        }

    fun getCityDataFromDB(city: String) =
        LitePal.where("cityName = ?", city)
            .find(Weather::class.java)
            .getOrNull(0)?.weatherData ?: ""

    fun getAllCityDataFromDB(): MutableList<Weather> = LitePal.findAll(Weather::class.java)

    fun setCityDataToDB(
        city: String,
        weatherData: String,
        call: String? = null,
        phoneNumber: String? = null
    ) {
        val weather = LitePal.where("cityName = ?", city)
            .find(Weather::class.java).getOrNull(0)
        if (weather == null) {
            Weather(city, weatherData).save()
        } else {
            weather.weatherData = weatherData
            if (call != null) {
                weather.call = call
            }
            if (phoneNumber != null) {
                weather.phoneNumber = phoneNumber
            }
            weather.saveOrUpdate("cityName = ?", city)
        }
        LitePal.findAllAsync(Weather::class.java).listen {
            if (it.size == 1) {
                defaultCity = city
            }
        }
    }

    fun removeCityFromDB(city: String) =
        LitePal.deleteAll(Weather::class.java, "cityName = ?", city)

    fun clearCache() {
        LitePal.deleteAllAsync(Weather::class.java)
        editor.clear()
        editor.apply()
    }
}
