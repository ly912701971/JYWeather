package com.jy.weather.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.RemoteViews
import com.jy.weather.JYApplication

/**
 * Widget基类
 *
 * Created by liyang
 * on 2019/3/22
 */
abstract class WeatherWidget : AppWidgetProvider() {

    protected var defaultCity: String? = null

    protected var remoteView: RemoteViews? = null

    override fun onUpdate(
        context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        updateWidget()
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        // 监听系统广播ACTION_TIME_TICK
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        JYApplication.INSTANCE.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (Intent.ACTION_TIME_TICK == intent?.action) {
            updateWidget()
        }
    }

    abstract fun updateWidget()
}