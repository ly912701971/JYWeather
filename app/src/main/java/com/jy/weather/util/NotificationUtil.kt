package com.jy.weather.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.activity.WeatherActivity

/**
 * 通知类工具
 *
 * Created by Yang on 2018/1/11.
 */

object NotificationUtil {

    /**
     * 打开通知栏
     */
    fun openNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager// 管理消息通知
        val notification: Notification
        var builder: Notification.Builder = Notification.Builder(context)
            .setSmallIcon(R.mipmap.ic_icon_round)
            .setTicker("卷云天气")
            .setWhen(System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 版本大于8.0，需要设置渠道name和id
            val channel = NotificationChannel("jy", "卷云天气", NotificationManager.IMPORTANCE_DEFAULT)
            // 取消震动
            channel.vibrationPattern = longArrayOf(0)
            builder = builder.setChannelId("jy")
            manager.createNotificationChannel(channel)
        }
        notification = builder.build()
        notification.flags = Notification.FLAG_ONGOING_EVENT// 该通知常驻在通知栏，始终存在

        // 传入当前项目的包名，和通知栏上要显示的自定义布局的ID
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_notification)
        // 下面是设置通知栏布局里面控件的属性
        val defaultCity = JYApplication.cityDB.defaultCity ?: "北京"
        val weather =
            JsonUtil.handleWeatherResponse(JYApplication.cityDB.getCityData(defaultCity))
        if (weather != null) {
            remoteViews.setTextViewText(R.id.city_name, defaultCity)// 常驻城市名称
            remoteViews.setImageViewResource(R.id.pic_bar,
                weather.now.nowIcon) // 要显示的天气图片
            remoteViews.setTextViewText(R.id.now_Temp,
                weather.now.temperature)// 当前温度
            remoteViews.setTextViewText(R.id.lowTemp,
                weather.dailyForecasts[0].minTemp)// 最低温度
            remoteViews.setTextViewText(R.id.highTemp,
                weather.dailyForecasts[0].maxTemp)// 最高温度
            remoteViews.setTextViewText(R.id.weatherDetail,
                weather.now.condText)// 天气情况
            remoteViews.setTextViewText(R.id.sun_rise_time,
                weather.dailyForecasts[0].sunRise)// 日出时间
            remoteViews.setTextViewText(R.id.sun_set_time,
                weather.dailyForecasts[0].sunSet)// 日落时间
            remoteViews.setTextViewText(R.id.update_time,
                weather.update.getLoc())// 更新时间
        }

        val intent = Intent(context, WeatherActivity::class.java)
        intent.putExtra("city", defaultCity)
        val pendingIntent = PendingIntent.getActivity(context, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // 使通知栏响应点击事件，进入MainActivity
        remoteViews.setOnClickPendingIntent(R.id.rl_notice_bar, pendingIntent)

        notification.contentView = remoteViews
        notification.contentIntent = pendingIntent
        manager.notify(R.string.app_name, notification)
    }

    fun cancelNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(R.string.app_name)
    }
}
