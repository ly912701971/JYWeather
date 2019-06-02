package com.jy.weather.viewmodel

import android.databinding.*
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.DailyForecast
import com.jy.weather.entity.HourlyForecast
import com.jy.weather.entity.Now
import com.jy.weather.navigator.WeatherNavigator
import com.jy.weather.util.*
import java.lang.ref.WeakReference

class WeatherViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    val bgResId: ObservableField<Int> = ObservableField()
    val currentCity: ObservableField<String> = ObservableField()
    val isRefresh: ObservableBoolean = ObservableBoolean(true)
    val isViewReady: ObservableBoolean = ObservableBoolean(false)
    val updateTime: ObservableField<String> = ObservableField()
    val iconId: ObservableField<Int> = ObservableField()
    val condText: ObservableField<String> = ObservableField()
    val windDirection: ObservableField<String> = ObservableField()
    val windScale: ObservableField<String> = ObservableField()
    val relativeHum: ObservableField<String> = ObservableField()
    val temperature: ObservableField<String> = ObservableField()
    val hourlyForecasts: ObservableList<HourlyForecast> = ObservableArrayList()
    val dailyForecasts: ObservableList<DailyForecast> = ObservableArrayList()
    val styleMap: ObservableMap<String, Pair<String, String>> = ObservableArrayMap()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    lateinit var dialogText: String
    lateinit var now: Now

    private lateinit var navigator: WeakReference<WeatherNavigator>

    fun start(navigator: WeatherNavigator) {
        this.navigator = WeakReference(navigator)
        bgResId.set(DrawableUtil.getBackground(db.condCode))
    }

    fun onNewIntent(city: String) {
        if (currentCity.get() == city) {
            return
        }
        currentCity.set(city)
        queryData(city)
    }

    fun onRefreshListener() {
        isRefresh.set(true)
        isViewReady.set(false)
        queryData(currentCity.get())
    }

    fun startTodayActivity() = navigator.get()?.startTodayActivity()

    fun startCityManageActivity() = navigator.get()?.startCityManageActivity()

    fun startSettingActivity() = navigator.get()?.startSettingActivity()

    fun startLiveWeatherActivity() = navigator.get()?.startLiveWeatherActivity()

    fun showLifestyleDialog(type: String) {
        dialogText = (styleMap[type] ?: return).second
        navigator.get()?.showLifestyleDialog()
    }

    private fun queryData(city: String?) {
        city ?: return
        if (!NetworkUtil.isNetworkAvailable()) {// 无网使用缓存数据
            isRefresh.set(false)
            snackbarObj.set(SnackbarObj(context.getString(R.string.network_unavailable)))
            handleData(db.getCityDataFromDB(city))
        } else {
            NetworkInterface.queryWeatherData(
                city,
                {
                    handleData(it)
                },
                {
                    isRefresh.set(false)
                    snackbarObj.set(SnackbarObj(context.getString(R.string.data_unavailable)))
                    handleData(db.getCityDataFromDB(city))
                }
            )
        }
    }

    private fun handleData(dataText: String?) {
        val weather = JsonUtil.handleWeatherResponse(dataText) ?: return
        if (weather.status == "ok") {
            // 处理数据
            currentCity.set(weather.basic.cityName)
            updateTime.set(weather.update.loc)
            now = weather.now
            handleNow(now)
            weather.lifestyles.forEach {
                styleMap[it.type] = Pair(it.brief, it.text)
            }
            dailyForecasts.apply {
                clear()
                addAll(weather.dailyForecasts)
            }
            hourlyForecasts.apply {
                clear()
                addAll(weather.hourlyForecasts)
            }

            if (db.notification) {// 打开通知栏
                NotificationUtil.openNotification()
            }
            if (db.autoUpdate) {// 打开自动更新服务
                navigator.get()?.startAutoUpdateService()
            }
            navigator.get()?.startSendMessageService()
        }
        isRefresh.set(false)
        isViewReady.set(true)
        navigator.get()?.startDataRefreshAnimator()
    }

    private fun handleNow(now: Now) {
        db.condCode = now.code
        bgResId.set(now.nowBackgroundId)
        iconId.set(now.nowIconId)
        condText.set(now.condText)
        windDirection.set(now.windDirection)
        windScale.set(now.windScale)
        relativeHum.set(now.relativeHum)
        temperature.set(now.temperature)
    }
}