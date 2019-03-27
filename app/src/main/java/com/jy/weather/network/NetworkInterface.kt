package com.jy.weather.network

import android.annotation.SuppressLint
import com.jy.weather.JYApplication
import com.jy.weather.util.HttpUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Callback
import okhttp3.Response

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private const val BASE = "https://free-api.heweather.com/s6/weather"
    private const val NOW = "/now"
    private const val FORECAST = "/forecast"
    private const val HOURLY = "/hourly"
    private const val LIFESTYLE = "/lifestyle"
    private const val LOCATION = "?location="
    private const val KEY = "&key=4d9d9383c876415a92bb9e2fddba0b15"

    fun queryWeatherDataAsync(city: String, callback: Callback) =
        HttpUtil.sendAsyncOkHttpRequest(BASE + LOCATION + city + KEY, callback)

    fun queryNowDataAsync(city: String, callback: Callback) =
        HttpUtil.sendAsyncOkHttpRequest(BASE + NOW + LOCATION + city + KEY, callback)

    fun queryForecastDataAsync(city: String, callback: Callback) =
        HttpUtil.sendAsyncOkHttpRequest(BASE + FORECAST + LOCATION + city + KEY, callback)

    fun queryHourlyDataAsync(city: String, callback: Callback) =
        HttpUtil.sendAsyncOkHttpRequest(BASE + HOURLY + LOCATION + city + KEY, callback)

    fun queryLifestyleDataAsync(city: String, callback: Callback) =
        HttpUtil.sendAsyncOkHttpRequest(BASE + LIFESTYLE + LOCATION + city + KEY, callback)

    @SuppressLint("CheckResult")
    @JvmOverloads
    fun queryWeatherData(
        city: String,
        onSuccess: (String?) -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) {
        Observable.create(ObservableOnSubscribe<Response> {
            it.onNext(HttpUtil.sendOkHttpRequest(BASE + LOCATION + city + KEY))
        }).map {
            return@map it.body()?.string()
        }.doOnNext {
            it ?: return@doOnNext
            JYApplication.cityDB.setCityData(city, it)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onFailure)
    }

    fun queryNowData(city: String) =
        HttpUtil.sendOkHttpRequest(BASE + NOW + LOCATION + city + KEY)

    fun queryForecastData(city: String) =
        HttpUtil.sendOkHttpRequest(BASE + FORECAST + LOCATION + city + KEY)

    fun queryHourlyData(city: String) =
        HttpUtil.sendOkHttpRequest(BASE + HOURLY + LOCATION + city + KEY)

    fun queryLifestyleData(city: String) =
        HttpUtil.sendOkHttpRequest(BASE + LIFESTYLE + LOCATION + city + KEY)
}
