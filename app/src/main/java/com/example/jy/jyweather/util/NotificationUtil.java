package com.example.jy.jyweather.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.activity.MainActivity;
import com.example.jy.jyweather.entity.WeatherBean;

/**
 * 通知类工具
 * <p>
 * Created by Yang on 2018/1/11.
 */

public class NotificationUtil {

    /**
     * 打开通知栏
     */
    public static void openNotification(Context context) {
        Notification notification = new Notification();
        // 设置图标，后面的自定义布局的图片会覆盖它，但还是得设置，不然不会显示到通知栏
        notification.icon = R.mipmap.ic_icon;
        notification.tickerText = context.getString(R.string.app_name);
        notification.when = System.currentTimeMillis();
        // 该通知常驻在通知栏，始终存在
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 传入当前项目的包名，和通知栏上要显示的自定义布局的ID
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_notification);
        // 下面是设置通知栏布局里面控件的属性
        String defaultCity = JYApplication.getInstance().getCityDB().getDefaultCity();
        WeatherBean weather = JsonUtil.handleWeatherResponse(
                JYApplication.getInstance().getCityDB().getData(defaultCity));
        if (weather != null) {
            remoteViews.setTextViewText(R.id.city_name, defaultCity);// 常驻城市名称
            remoteViews.setImageViewResource(R.id.pic_bar,
                    DrawableUtil.getCondIcon(weather.getNow().getCode())); // 要显示的天气图片
            remoteViews.setTextViewText(R.id.now_Temp,
                    weather.getNow().getTemperature().concat(context.getString(R.string.degree)));// 当前温度
            remoteViews.setTextViewText(R.id.lowTemp,
                    weather.getDailyForecasts().get(0).getMinTemp().concat(context.getString(R.string.degree)));// 最低温度
            remoteViews.setTextViewText(R.id.highTemp,
                    weather.getDailyForecasts().get(0).getMaxTemp().concat(context.getString(R.string.degree)));// 最高温度
            remoteViews.setTextViewText(R.id.weatherDetail,
                    weather.getNow().getCondText());// 天气情况
            remoteViews.setTextViewText(R.id.sun_rise_time,
                    weather.getDailyForecasts().get(0).getSunRise());// 日出时间
            remoteViews.setTextViewText(R.id.sun_set_time,
                    weather.getDailyForecasts().get(0).getSunSet());// 日落时间
            remoteViews.setTextViewText(R.id.update_time,
                    weather.getUpdate().getLoc().split(" ")[1]);// 更新时间
        }


        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("city", defaultCity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 使通知栏响应点击事件，进入MainActivity
        remoteViews.setOnClickPendingIntent(R.id.rl_notice_bar, pendingIntent);
        notification.contentView = remoteViews;
        notification.contentIntent = pendingIntent;

        // 管理消息通知
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(R.string.app_name, notification);
        }
    }

    public static void cancelNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(R.string.app_name);
        }
    }

}
