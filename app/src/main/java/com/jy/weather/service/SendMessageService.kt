package com.jy.weather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jy.weather.receiver.SendMessageReceiver
import java.util.*

class SendMessageService : Service() {

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val now = Calendar.getInstance()
        val targetTime = now.clone() as Calendar
        targetTime.apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (targetTime.before(now)) {
                targetTime.add(Calendar.DATE, 1)
            }
        }
        val pi = PendingIntent.getBroadcast(
            this,
            "SendMessageService".hashCode(),
            Intent(this, SendMessageReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .setExact(AlarmManager.RTC_WAKEUP, targetTime.timeInMillis, pi)
        Log.e("SendMessageService", targetTime.timeInMillis.toString())
        return super.onStartCommand(intent, flags, startId)
    }
}