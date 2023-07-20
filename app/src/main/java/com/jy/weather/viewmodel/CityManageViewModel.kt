package com.jy.weather.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.entity.WeatherData
import com.jy.weather.navigator.CityManageNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference

class CityManageViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private val cityList = db.getAllCityDataFromDB()

    private lateinit var navigator: WeakReference<CityManageNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val cities: ObservableList<WeatherData> = ObservableArrayList()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: CityManageNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun initData() {
        for ((_, weatherData) in cityList) {
            JsonUtil.handleWeatherData(weatherData)?.let {
                cities.add(it)
            }
        }
    }

    fun onItemClick(index: Int) = navigator.get()
        ?.startWeatherActivity(cities[index].city, cities[index].cityId, cities[index].location)

    fun onMenuItemClick(index: Int, position: Int): Boolean {
        val cityId = cities[index].cityId
        val defaultCity = db.defaultCity
        when (position) {
            // 点击"常驻"
            0 ->
                if (cityId != defaultCity) {
                    db.defaultCity = cityId
                    cities[0] = cities[0]// 模仿一次数据改变，触发notifyDataSetChanged()
                } else {
                    snackbarObj.set(SnackbarObj(context.getString(R.string.already_resident_city)))
                }

            // 点击"删除"
            1 ->
                if (cityId == defaultCity) {
                    snackbarObj.set(SnackbarObj(context.getString(R.string.keep_resident_city)))
                } else {
                    for (data in cities) {
                        if (data.cityId == cityId) {
                            cities.remove(data)
                            break
                        }
                    }
                    for (data in cityList) {
                        if (data.cityId == cityId) {
                            cityList.remove(data)
                            break
                        }
                    }
                    db.removeCityFromDB(cityId)
                }
        }
        return false// 关闭菜单
    }
}