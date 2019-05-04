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

    private val citySet = db.citySet

    private lateinit var navigator: WeakReference<CityManageNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val cities: ObservableList<CityData> = ObservableArrayList()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: CityManageNavigator) {
        this.navigator = WeakReference(navigator)

        var weather: Weather?
        for (city in citySet) {
            weather = JsonUtil.handleWeatherResponse(db.getCityData(city))
            if (weather != null) {
                cities.add(
                    CityData(
                        weather.basic.cityName,
                        weather.now.nowIconId,
                        "${weather.basic.parentCity} - ${weather.basic.adminArea}",
                        "${weather.dailyForecasts[0].minTemp} ~ ${weather.dailyForecasts[0].maxTemp}C"
                    )
                )
            }
        }
    }

    fun onItemClick(index: Int) = navigator.get()?.jumpToCity(cities[index].city)

    fun onMenuItemClick(index: Int, position: Int): Boolean {
        val city = cities[index].city
        val defaultCity = JYApplication.cityDB.defaultCity
        when (position) {
            // 点击"常驻"
            0 -> if (city != defaultCity) {
                JYApplication.cityDB.defaultCity = city
                cities[0] = cities[0]// 模仿一次数据改变，触发notifyDataSetChanged()
            } else {
                snackbarObj.set(SnackbarObj(context.getString(R.string.already_resident_city)))
            }

            // 点击"删除"
            1 -> when {
                city == defaultCity ->
                    snackbarObj.set(SnackbarObj(context.getString(R.string.keep_resident_city)))
                citySet.size == 1 ->
                    snackbarObj.set(SnackbarObj(context.getString(R.string.keep_one_city)))
                else -> {
                    for (data in cities) {
                        if (data.city == city) {
                            cities.remove(data)
                            break
                        }
                    }
                    citySet.remove(city)
                    db.removeCity(city)
                }
            }
        }
        return false// 关闭菜单
    }
}