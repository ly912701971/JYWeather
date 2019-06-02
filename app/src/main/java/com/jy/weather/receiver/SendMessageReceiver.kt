package com.jy.weather.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.jy.weather.JYApplication
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.service.SendMessageService
import com.jy.weather.util.StringUtil

class SendMessageReceiver : BroadcastReceiver() {

    private val smsManager by lazy { SmsManager.getDefault() }

    override fun onReceive(context: Context?, intent: Intent?) {
        traverse()
        context?.startService(Intent(context, SendMessageService::class.java))
    }

    private fun traverse() {
        val cityList = JYApplication.cityDB.getAllCityDataFromDB()
        for (city in cityList) {
            if (city.call.isNotEmpty() && city.phoneNumber.isNotEmpty()) {
                NetworkInterface.queryWeatherData(
                    city.cityName,
                    {
                        sendMessage(city.phoneNumber, StringUtil.getMessage(city.call, it))
                    }
                )
            }
        }
    }

    private fun sendMessage(phoneNumber: String, message: String) =
        smsManager.sendTextMessage(
            phoneNumber,
            null,
            message,
            null,
            null
        )
}