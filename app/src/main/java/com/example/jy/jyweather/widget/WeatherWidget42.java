package com.example.jy.jyweather.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * 桌面插件4x2
 * Created by txh on 2018/1/8.
 */

public class WeatherWidget42 extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateMyWidget(context, appWidgetManager);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        updateMyWidget(context, appWidgetManager);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 更新桌面插件的内容
     */
    public void updateMyWidget(Context context, AppWidgetManager appWidgetManager) {

    }
}
