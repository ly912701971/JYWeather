package com.jy.weather.db

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

/**
 * 伪城市数据库
 * 利用SharedPreferences实现
 *
 * Created by Yang on 2018/1/9.
 */
class CityDB(context: Context) {

    companion object {
        private const val DB_BASE_PATH = "city_database"
        private const val DB_CITY_SET = "city_set"
        private const val DB_DEFAULT_CITY = "default_city"
        private const val DB_COND_CODE = "cond_code"
        private const val DB_NOTIFICATION = "notification"
        private const val DB_AUTO_UPDATE = "auto_update"
        private const val DB_UPDATE_INTERVAL = "update_interval"
    }

    private val sp: SharedPreferences
    private var editor: Editor

    init {
        sp = context.getSharedPreferences(DB_BASE_PATH, Context.MODE_PRIVATE)
        editor = sp.edit()
    }

    var citySet: Set<String>?
        get() = sp.getStringSet(DB_CITY_SET, setOf())
        set(citySet) {
            editor.putStringSet(DB_CITY_SET, citySet)
            editor.apply()
        }

    var defaultCity: String?
        get() = sp.getString(DB_DEFAULT_CITY, null)
        set(defaultCity) {
            editor.putString(DB_DEFAULT_CITY, defaultCity)
            editor.apply()
        }

    var condCode: String?
        get() = sp.getString(DB_COND_CODE, null)
        set(condCode) {
            editor.putString(DB_COND_CODE, condCode)
            editor.apply()
        }

    // 默认不打开通知栏
    var notification: Boolean
        get() = sp.getBoolean(DB_NOTIFICATION, false)
        set(notification) {
            editor.putBoolean(DB_NOTIFICATION, notification)
            editor.apply()
        }

    // 默认不自动更新
    var autoUpdate: Boolean
        get() = sp.getBoolean(DB_AUTO_UPDATE, false)
        set(autoUpdate) {
            editor.putBoolean(DB_AUTO_UPDATE, autoUpdate)
            editor.apply()
        }

    // 默认更新时间为2小时
    var updateInterval: Int
        get() = sp.getInt(DB_UPDATE_INTERVAL, 2)
        set(hour) {
            editor.putInt(DB_UPDATE_INTERVAL, hour)
            editor.apply()
        }

    fun getData(key: String): String? {
        return sp.getString(key, null)
    }

    fun setCityData(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun removeCity(city: String) {
        editor.remove(city)
        editor.apply()
    }

    fun clearCache() {
        editor.clear()
        editor.apply()
    }
}
