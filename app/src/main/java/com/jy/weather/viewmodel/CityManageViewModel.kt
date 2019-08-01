package com.jy.weather.viewmodel

import android.app.AlertDialog
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
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

    companion object {
        private const val PHONE_NUMBER_REGEX = "^1([34578])\\d{9}\$"
    }

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private val cityList = db.getAllCityDataFromDB()
    private val phoneNumberRegex by lazy { Regex(PHONE_NUMBER_REGEX) }

    private lateinit var navigator: WeakReference<CityManageNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val cities: ObservableList<CityData> = ObservableArrayList()
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: CityManageNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun initData() {
        var weather: Weather?
        for ((cityName, weatherData, call, phoneNumber) in cityList) {
            weather = JsonUtil.handleWeatherResponse(weatherData)
            if (weather != null) {
                cities.add(
                    CityData(
                        cityName,
                        weather.now.nowIconId,
                        "${weather.basic.parentCity} - ${weather.basic.adminArea}",
                        "${weather.dailyForecasts[0].minTemp} ~ ${weather.dailyForecasts[0].maxTemp}C",
                        call,
                        phoneNumber
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

            // 点击亲情号码
            1 -> navigator.get()?.requestPermission(index)

            // 点击"删除"
            2 ->
                if (city == defaultCity) {
                    snackbarObj.set(SnackbarObj(context.getString(R.string.keep_resident_city)))
                } else {
                    for (data in cities) {
                        if (data.city == city) {
                            cities.remove(data)
                            break
                        }
                    }
                    for (data in cityList) {
                        if (data.cityName == city) {
                            cityList.remove(data)
                            break
                        }
                    }
                    db.removeCityFromDB(city)
                }
        }
        return false// 关闭菜单
    }

    fun dealFamilyNumber(index: Int, call: String, phoneNumber: String, dialog: AlertDialog) {
        when {
            phoneNumber.isEmpty() -> {
                db.setCityDataToDB(
                    cityList[index].cityName,
                    cityList[index].weatherData,
                    "",
                    ""
                )
                cities[index].call = ""
                cities[index].phoneNumber = ""
                dialog.dismiss()
                snackbarObj.set(SnackbarObj(context.getString(R.string.family_number_clear)))
            }

            else -> {
                when {
                    !phoneNumberRegex.matches(phoneNumber) ->
                        snackbarObj.set(SnackbarObj(context.getString(R.string.phone_number_not_valid)))

                    call.isEmpty() ->
                        snackbarObj.set(SnackbarObj(context.getString(R.string.call_empty)))

                    else -> {
                        if (call == cities[index].call && phoneNumber == cities[index].phoneNumber) {
                            dialog.dismiss()
                            return
                        }
                        db.setCityDataToDB(
                            cityList[index].cityName,
                            cityList[index].weatherData,
                            call,
                            phoneNumber
                        )
                        cities[index].call = call
                        cities[index].phoneNumber = phoneNumber
                        dialog.dismiss()
                        snackbarObj.set(SnackbarObj(context.getString(R.string.family_number_associate_success)))
                    }
                }
            }
        }
    }
}