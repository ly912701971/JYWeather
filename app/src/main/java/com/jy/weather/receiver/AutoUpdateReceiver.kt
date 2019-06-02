package com.jy.weather.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jy.weather.JYApplication
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.NetworkUtil

class AutoUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        updateWeather()
        context?.startService(Intent(context, AutoUpdateService::class.java))
    }

    private fun updateWeather() {
        val cityList = JYApplication.cityDB.getAllCityDataFromDB()
        if (NetworkUtil.isNetworkAvailable()) {
            cityList.forEach {
                NetworkInterface.queryWeatherData(it.cityName)
            }
        }
    }
}