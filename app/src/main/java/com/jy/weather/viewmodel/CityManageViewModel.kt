package com.jy.weather.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import com.jy.weather.JYApplication
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

    private lateinit var citySet: HashSet<String>
    private val cityDB = JYApplication.cityDB
    private val navigator = WeakReference<CityManageNavigator>(navigator)

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
            }

            // 点击"删除"
            1 -> when {
                citySet.size == 1 -> snackbarObj.set(SnackbarObj("请至少保留一个城市"))
                city == defaultCity -> snackbarObj.set(SnackbarObj("您不能删除常驻城市"))
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