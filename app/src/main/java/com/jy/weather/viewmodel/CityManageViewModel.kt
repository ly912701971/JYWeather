package com.jy.weather.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.entity.CityData
import com.jy.weather.entity.Weather
import com.jy.weather.navigator.CityManageNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference

class CityManageViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private val cityData = db.getAllCityDataFromDB()

    private lateinit var navigator: WeakReference<CityManageNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val cities: ObservableList<CityData> = ObservableArrayList()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: CityManageNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun initData() {
        var weather: Weather?
        for ((cityName, weatherData) in cityData) {
            weather = JsonUtil.handleWeatherResponse(weatherData)
            if (weather != null) {
                cities.add(
                    CityData(
                        cityName,
                        weather.now.nowIconId,
                        "${weather.basic.parentCity} - ${weather.basic.adminArea}",
                        "${weather.dailyForecasts[0].minTemp} ~ ${weather.dailyForecasts[0].maxTemp}C"
                    )
                )
            }
        }
    }

    fun onItemClick(index: Int) = navigator.get()?.startWeatherActivity(cities[index].city)

    fun onMenuItemClick(index: Int, position: Int): Boolean {
        val city = cities[index].city
        val defaultCity = JYApplication.cityDB.defaultCity
        when (position) {
            // 点击"常驻"
            0 ->
                if (city != defaultCity) {
                    JYApplication.cityDB.defaultCity = city
                    cities[0] = cities[0]// 模仿一次数据改变，触发notifyDataSetChanged()
                } else {
                    snackbarObj.set(SnackbarObj(context.getString(R.string.already_resident_city)))
                }

            // 点击"删除"
            1 ->
                if (city == defaultCity) {
                    snackbarObj.set(SnackbarObj(context.getString(R.string.keep_resident_city)))
                } else {
                    for (data in cities) {
                        if (data.city == city) {
                            cities.remove(data)
                            break
                        }
                    }
                    for (data in cityData) {
                        if (data.cityName == city) {
                            cityData.remove(data)
                            break
                        }
                    }
                    db.removeCityFromDB(city)
                }
        }
        return false// 关闭菜单
    }
}