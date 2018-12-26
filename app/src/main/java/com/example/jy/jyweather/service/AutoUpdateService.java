package com.example.jy.jyweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.network.NetworkInterface;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int updateInterval = JYApplication.getInstance().getCityDB().getUpdateInterval() * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + updateInterval;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        Set<String> citySet = JYApplication.getInstance().getCityDB().getCitySet();
        for (final String city : citySet) {
            NetworkInterface.queryWeatherData(city, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    JYApplication.getInstance().getCityDB().setCityData(city, response.body().string());
                }
            });
        }
    }
}
