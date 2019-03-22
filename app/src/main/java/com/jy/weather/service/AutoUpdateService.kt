package com.jy.weather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import com.jy.weather.JYApplication
import com.jy.weather.network.NetworkInterface
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class AutoUpdateService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        updateWeather()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val updateInterval = JYApplication.cityDB.updateInterval * 60 * 60 * 1000
        val triggerAtTime = SystemClock.elapsedRealtime() + updateInterval
        val i = Intent(this, AutoUpdateService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, i, 0)
        alarmManager.cancel(pendingIntent)
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateWeather() {
        val citySet = HashSet<String>(JYApplication.cityDB.citySet)
        for (city in citySet) {
            NetworkInterface.queryWeatherData(city, object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body() ?: return
                    JYApplication.cityDB.setCityData(city, body.string())
                }
            })
        }
    }
}
