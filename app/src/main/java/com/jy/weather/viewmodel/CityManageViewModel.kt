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

class CityManageViewModel(
    navigator: CityManageNavigator
) {

    private val context = JYApplication.INSTANCE
    private val cityDB = JYApplication.cityDB
    private val navigator = WeakReference<CityManageNavigator>(navigator)

    private lateinit var citySet: HashSet<String>

    val bgResId: ObservableField<Int> = ObservableField()
    val cities: ObservableList<CityData> = ObservableArrayList()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start() {
        bgResId.set(DrawableUtil.getBackground(cityDB.condCode))
        citySet = cityDB.citySet

        var weatherData: String?
        var weather: Weather?
        var cityData: CityData
        for (city in citySet) {
            weatherData = cityDB.getCityData(city)
            weather = JsonUtil.handleWeatherResponse(weatherData)
            if (weather != null) {
                cityData = CityData(
                    weather.basic.cityName,
                    weather.now.nowIcon,
                    "${weather.basic.parentCity} - ${weather.basic.adminArea}",
                    "${weather.dailyForecasts[0].minTemp} ~ ${weather.dailyForecasts[0].maxTemp}C"
                )
                cities.add(cityData)
            }
        }
    }

    fun onItemClick(index: Int) {
        navigator.get()?.jumpToCity(cities[index].city)
    }

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
                    cityDB.removeCity(city)
                }
            }
        }
        return false// 关闭菜单
    }
}