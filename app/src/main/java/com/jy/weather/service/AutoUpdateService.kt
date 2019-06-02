package com.jy.weather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import com.jy.weather.JYApplication
import com.jy.weather.receiver.AutoUpdateReceiver

class AutoUpdateService : Service() {

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val triggerAtTime = System.currentTimeMillis() +
            JYApplication.cityDB.updateInterval * 60 * 60 * 1000
        val pi = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, AutoUpdateReceiver::class.java),
            0
        )
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi)
        return super.onStartCommand(intent, flags, startId)
    }
}
