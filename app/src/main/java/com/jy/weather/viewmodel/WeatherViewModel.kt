package com.jy.weather.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.lifecycleScope
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.Daily
import com.jy.weather.entity.Hourly
import com.jy.weather.entity.Index
import com.jy.weather.entity.Location
import com.jy.weather.entity.WeatherData
import com.jy.weather.navigator.WeatherNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class WeatherViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val isRefresh: ObservableBoolean = ObservableBoolean(true)
    val isViewReady: ObservableBoolean = ObservableBoolean(false)
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    val currentCity: ObservableField<String> = ObservableField()
    val weather: ObservableField<WeatherData> = ObservableField()
    val hourly: ObservableList<Hourly> = ObservableArrayList()
    val daily: ObservableList<Daily> = ObservableArrayList()
    val index: ObservableList<Index> = ObservableArrayList()

    lateinit var dialogText: String
    private lateinit var navigator: WeakReference<WeatherNavigator>
    private var curCityId = ""
    private var curLocation: Location? = null

    fun start(navigator: WeatherNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun onNewIntent(city: String, cityId: String, location: Location?) {
        if (currentCity.get() == city) {
            return
        }
        currentCity.set(city)
        this.curCityId = cityId
        this.curLocation = location
        queryData()
    }

    fun onRefreshListener() {
        isRefresh.set(true)
        isViewReady.set(false)
        queryData()
    }

    fun startTodayActivity() = navigator.get()?.startTodayActivity()

    fun startCityManageActivity() = navigator.get()?.startCityManageActivity()

    fun startSettingActivity() = navigator.get()?.startSettingActivity()

    fun startLiveWeatherActivity() = navigator.get()?.startLiveWeatherActivity()

    fun showLifestyleDialog(index: Int) {
        dialogText = this.index.getOrNull(index)?.text ?: return
        navigator.get()?.showLifestyleDialog()
    }

    private fun queryData() {
        val city = currentCity.get() ?: return
        val cityId = curCityId
        if (!NetworkUtil.isNetworkAvailable()) {// 无网使用缓存数据
            onRequestError(cityId)
        } else {
            navigator.get()?.getActivity()?.lifecycleScope?.launch(Dispatchers.IO) {
                val nowReq = async { NetworkInterface.queryNowData(cityId) }
                val hourlyReq = async { NetworkInterface.queryHourlyData(cityId) }
                val dailyReq = async { NetworkInterface.queryDailyData(cityId) }
                val indexReq = async { NetworkInterface.queryIndexData(cityId) }
                val locationReq = if (curLocation == null) {
                    async { NetworkInterface.queryLocationData(cityId) }
                } else {
                    null
                }
                val weather = withTimeoutOrNull(3000) {
                    WeatherData().apply {
                        this.city = city
                        this.cityId = cityId
                        nowReq.await()?.let { now = JsonUtil.handleNowData(it)?.data }
                        hourlyReq.await()?.let { hourly = JsonUtil.handleHourlyData(it)?.data }
                        dailyReq.await()?.let { daily = JsonUtil.handleDailyData(it)?.data }
                        indexReq.await()?.let { index = JsonUtil.handleIndexData(it)?.data }
                        if (locationReq != null) {
                            locationReq.await()?.let {
                                location = JsonUtil.handleLocationData(it)?.data?.get(0)
                                this.city = location?.name ?: ""
                            }
                        } else {
                            this.location = curLocation
                        }
                        db.setCityDataToDB(cityId, JsonUtil.gson.toJson(this))
                    }
                }
                if (weather == null) {
                    onRequestError(cityId)
                } else {
                    onRequestSuccess(weather)
                }
            }
        }
    }

    private fun onRequestSuccess(data: WeatherData) {
        weather.set(data)
        data.now?.let {
            bgResId.set(it.bdId)
            db.condCode = it.icon
        }
        data.hourly?.let {
            hourly.run {
                clear()
                addAll(it)
            }
        }
        data.daily?.let {
            daily.run {
                clear()
                addAll(it)
            }
        }
        data.index?.let {
            index.run {
                clear()
                addAll(it)
            }
        }
        data.location?.let {
            currentCity.set(it.name)
        }

        isRefresh.set(false)
        isViewReady.set(true)
        navigator.get()?.startDataRefreshAnimator()
    }

    private fun onRequestError(cityId: String) {
        isRefresh.set(false)
        snackbarObj.set(SnackbarObj(context.getString(R.string.data_unavailable)))
        JsonUtil.handleWeatherData(db.getCityDataFromDB(cityId))?.let {
            onRequestSuccess(it)
        }
    }
}