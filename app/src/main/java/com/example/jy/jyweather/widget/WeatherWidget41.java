package com.example.jy.jyweather.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.activity.WeatherActivity;
import com.example.jy.jyweather.entity.WeatherBean;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.JsonUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 桌面插件4x1
 * <p>
 * Created by txh on 2018/1/7.
 */

public class WeatherWidget41 extends AppWidgetProvider {

    private String defaultCity;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        handler.sendEmptyMessage(0);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // 监听系统广播ACTION_TIME_TICK
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        JYApplication.getInstance().registerReceiver(WeatherWidget41.this, intentFilter);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            RemoteViews remoteView = new RemoteViews(JYApplication.getInstance().getPackageName(), R.layout.widget_desktop_4x1);
            remoteView.setTextViewText(R.id.tv_time,
                    new SimpleDateFormat("HH:mm", Locale.CHINA).format(System.currentTimeMillis()));
            String city = JYApplication.getInstance().getCityDB().getDefaultCity();
            if (city != null && !city.equals(defaultCity)) {// 默认城市没有变化
                defaultCity = city;
                WeatherBean weather = JsonUtil.handleWeatherResponse(
                        JYApplication.getInstance().getCityDB().getData(defaultCity));
                if (weather != null) {
                    String tempScope = weather.getDailyForecasts().get(0).getMinTemp()
                            .concat(" ~ ")
                            .concat(weather.getDailyForecasts().get(0).getMaxTemp())
                            .concat(JYApplication.getInstance().getString(R.string.c_degree));
                    remoteView.setTextViewText(R.id.tv_city, city);
                    remoteView.setImageViewResource(R.id.iv_weather_icon,
                            DrawableUtil.getCondIcon(weather.getNow().getCode()));
                    remoteView.setTextViewText(R.id.tv_cond, weather.getNow().getCondText());
                    remoteView.setTextViewText(R.id.tv_now_temp,
                            weather.getNow().getTemperature().concat(JYApplication.getInstance().getString(R.string.c_degree)));
                    remoteView.setTextViewText(R.id.tv_temp_scope, tempScope);
                    remoteView.setTextViewText(R.id.tv_date, weather.getDailyForecasts().get(0).getDate());
                }
            }

            // 使桌面插件响应点击事件，进入MainActivity
            Intent intent = new Intent(JYApplication.getInstance(), WeatherActivity.class);
            intent.putExtra("city", defaultCity);
            PendingIntent pendingIntent = PendingIntent.getActivity(JYApplication.getInstance(), 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.ll_desktop_widget, pendingIntent);

            // 更新桌面插件
            ComponentName componentName = new ComponentName(JYApplication.getInstance(), WeatherWidget41.class);
            AppWidgetManager.getInstance(JYApplication.getInstance()).updateAppWidget(componentName, remoteView);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            handler.sendEmptyMessage(0);
        }
    }
}
