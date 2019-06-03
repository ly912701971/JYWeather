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

    private val context = JYApplication.INSTANCE
    private val manager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 打开通知栏
     */
    fun openNotification() {
        val builder = Notification.Builder(context)
            .setSmallIcon(R.mipmap.ic_icon_round)
            .setTicker("卷云天气")
            .setWhen(System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 版本大于8.0，需要设置渠道name和id
            val channel =
                NotificationChannel("jy", "卷云天气常驻", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    // 取消震动
                    vibrationPattern = longArrayOf(0)
                }
            builder.setChannelId("jy")
            manager.createNotificationChannel(channel)
        }
        val notification = builder.build().apply {
            flags = Notification.FLAG_ONGOING_EVENT// 该通知常驻在通知栏，始终存在
        }

        // 传入当前项目的包名，和通知栏上要显示的自定义布局的ID
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_notification)
        // 设置通知栏布局里面控件的属性
        val defaultCity = JYApplication.cityDB.defaultCity ?: "北京"
        val weather = JsonUtil.handleWeatherResponse(JYApplication.cityDB.getCityDataFromDB(defaultCity))
        if (weather != null) {
            remoteViews.apply {
                setTextViewText(R.id.city_name, defaultCity)// 常驻城市名称
                setImageViewResource(R.id.pic_bar, weather.now.nowIconId) // 要显示的天气图片
                setTextViewText(R.id.now_Temp, weather.now.temperature)// 当前温度
                setTextViewText(R.id.lowTemp, weather.dailyForecasts[0].minTemp)// 最低温度
                setTextViewText(R.id.highTemp, weather.dailyForecasts[0].maxTemp)// 最高温度
                setTextViewText(R.id.weatherDetail, weather.now.condText)// 天气情况
                setTextViewText(R.id.sun_rise_time, weather.dailyForecasts[0].sunRise)// 日出时间
                setTextViewText(R.id.sun_set_time, weather.dailyForecasts[0].sunSet)// 日落时间
                setTextViewText(R.id.update_time, weather.update.loc)// 更新时间
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, WeatherActivity::class.java).apply { putExtra("city", defaultCity) },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // 使通知栏响应点击事件，进入MainActivity
        remoteViews.setOnClickPendingIntent(R.id.rl_notice_bar, pendingIntent)

        manager.notify("卷云天气常驻".hashCode(), notification.apply {
            contentView = remoteViews
            contentIntent = pendingIntent
        })
    }

    fun cancelNotification() = manager.cancel("卷云天气常驻".hashCode())

    fun showNotification(city: String, message: String) {
        val builder = Notification.Builder(context)
            .setSmallIcon(R.mipmap.ic_icon_round)
            .setTicker("你有一条新的天气信息")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentTitle(city)
            .setContentText(message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 版本大于8.0，需要设置渠道name和id
            val channel =
                NotificationChannel("jy", "卷云天气通知", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    // 取消震动
                    vibrationPattern = longArrayOf(0)
                }
            builder.setChannelId("jy")
            manager.createNotificationChannel(channel)
        }
        val pi = PendingIntent.getActivity(
            context,
            0,
            Intent(context, WeatherActivity::class.java).apply { putExtra("city", city) },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pi)
        val notification = builder.build().apply {
            flags = Notification.FLAG_AUTO_CANCEL// 该通知常驻在通知栏，始终存在
        }
        manager.notify("卷云天气通知".hashCode(), notification)
    }
}
