package com.jy.weather.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.widget.RemoteViews
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.activity.WeatherActivity
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.StringUtil

/**
 * 桌面插件5x1
 *
 * Created by txh on 2018/1/7.
 */

class WeatherWidget51 : WeatherWidget() {

    override fun updateWidget() {
        if (remoteView == null) {
            remoteView = RemoteViews(JYApplication.INSTANCE.packageName, R.layout.widget_desktop_5x1)
        }

        val view = remoteView ?: return
        view.setTextViewText(R.id.tv_time, StringUtil.getTime())
        val city = JYApplication.cityDB.defaultCity ?: "北京"
        if (city != defaultCity) {// 城市变化
            defaultCity = city
            val weather = JsonUtil.handleWeatherResponse(JYApplication.cityDB.getCityDataFromDB(city))
                ?: return
            view.setTextViewText(R.id.tv_city, city)
            view.setImageViewResource(R.id.iv_weather_icon, DrawableUtil.getCondIcon(weather.now.code))
            view.setTextViewText(R.id.tv_cond, weather.now.condText)
            view.setTextViewText(R.id.tv_now_temp,
                weather.now.temperature + JYApplication.INSTANCE.getString(R.string.c_degree))
            view.setTextViewText(R.id.tv_temp_scope,
                "${weather.dailyForecasts[0].minTemp} ~ ${weather.dailyForecasts[0].maxTemp}℃")
            view.setTextViewText(R.id.tv_date, weather.dailyForecasts[0].date)
        }

        // 使桌面插件响应点击事件，进入MainActivity
        val intent = Intent(JYApplication.INSTANCE, WeatherActivity::class.java)
        intent.putExtra("city", defaultCity)
        val pendingIntent = PendingIntent.getActivity(JYApplication.INSTANCE,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.ll_desktop_widget, pendingIntent)

        // 更新桌面插件
        val componentName = ComponentName(JYApplication.INSTANCE, WeatherWidget51::class.java)
        AppWidgetManager.getInstance(JYApplication.INSTANCE).updateAppWidget(componentName, view)
    }
}
