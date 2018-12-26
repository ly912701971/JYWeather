package com.example.jy.jyweather.network;

import com.example.jy.jyweather.util.HttpUtil;

import okhttp3.Callback;

/**
 * 网络接口
 * <p>
 * Created by Yang on 2017/10/15.
 */

public class NetworkInterface {

    private static final String BASE = "https://free-api.heweather.com/s6/weather";

    private static final String NOW = "/now";

    private static final String FORECAST = "/forecast";

    private static final String HOURLY = "/hourly";

    private static final String LIFESTYLE = "/lifestyle";

    private static final String LOCATION = "?location=";

    private static final String KEY = "&key=4d9d9383c876415a92bb9e2fddba0b15";

    public static void queryWeatherData(String city, Callback callback) {
        HttpUtil.sendOkHttpRequest(BASE + LOCATION + city + KEY, callback);
    }

    public void queryNowData(String city, Callback callback) {
        HttpUtil.sendOkHttpRequest(BASE + NOW + LOCATION + city + KEY, callback);
    }

    public void queryForecastData(String city, Callback callback) {
        HttpUtil.sendOkHttpRequest(BASE + FORECAST + LOCATION + city + KEY, callback);
    }

    public void queryHourlyData(String city, Callback callback) {
        HttpUtil.sendOkHttpRequest(BASE + HOURLY + LOCATION + city + KEY, callback);
    }

    public void queryLifestyleData(String city, Callback callback) {
        HttpUtil.sendOkHttpRequest(BASE + LIFESTYLE + LOCATION + city + KEY, callback);
    }
}
