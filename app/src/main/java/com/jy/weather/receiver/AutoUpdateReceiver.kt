package com.jy.weather.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jy.weather.JYApplication
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.NotificationUtil
import com.jy.weather.util.StringUtil

class AutoUpdateReceiver : BroadcastReceiver() {

    private val db = JYApplication.cityDB

    override fun onReceive(context: Context?, intent: Intent?) {
        updateWeather()
        context?.startService(Intent(context, AutoUpdateService::class.java))
    }

    private fun updateWeather() {
        val cityList = db.getAllCityDataFromDB()
        if (NetworkUtil.isNetworkAvailable()) {
            for ((cityName, _, _, _) in cityList) {
                NetworkInterface.queryWeatherData(
                    cityName,
                    {
                        if (cityName == db.defaultCity && db.smartRemind) {
                            NotificationUtil.showNotification(cityName, StringUtil.getNotification(it))
                        }
                    }
                )
            }
        }
    }
}