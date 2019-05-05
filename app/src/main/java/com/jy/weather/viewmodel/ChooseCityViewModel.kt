package com.jy.weather.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.text.Editable
import android.view.View
import android.widget.TextView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GpsUtil
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference

class ChooseCityViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private val nationalCityList: Array<String> by lazy {
        context.resources.getStringArray(R.array.national_cities_list)
    }
    private val locateUnknown: String by lazy {
        context.getString(R.string.locate_unknown)
    }

    private lateinit var locationClient: LocationClient
    private lateinit var navigator: WeakReference<ChooseCityNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val location: ObservableField<String> = ObservableField(context.getString(R.string.locating))
    val searchResult: ObservableList<String> = ObservableArrayList()
    val hasSearch: ObservableBoolean = ObservableBoolean(false)
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    var hasGranted: Boolean = false

    fun start(navigator: ChooseCityNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun afterTextChanged(editable: Editable) {
        if (editable.isNotEmpty()) {
            searchResult.run {
                clear()
                addAll(nationalCityList.filter {
                    it.contains(editable)
                })
            }
            hasSearch.set(true)
        } else {
            hasSearch.set(false)
        }
    }

    fun locate() {
        if (GpsUtil.isOpen(context)) {
            locationClient = LocationClient(context).apply {
                registerLocationListener(MyLocationListener())
                locOption = LocationClientOption().apply {
                    setIsNeedAddress(true)
                    isOpenGps = true
                    timeOut = 4000
                }
                start()
            }
        } else {
            setLocation(locateUnknown)
            showGpsNotOpen()
        }
    }

    fun permissionDenied() = setLocation(locateUnknown)

    fun cityClicked(v: View) {
        if (!NetworkUtil.isNetworkAvailable()) {
            snackbarObj.set(SnackbarObj(context.getString(R.string.network_unavailable)))
            return
        }

        val city = when (v.id) {
            R.id.tv_location -> if (!hasGranted) {
                snackbarObj.set(SnackbarObj(context.getString(R.string.permission_denied)))
                return
            } else if (!GpsUtil.isOpen(context)) {
                showGpsNotOpen()
                return
            } else {
                (v as TextView).text.toString()
            }

            else -> (v as TextView).text.toString()
        }.replace(Regex("[市县区]"), "")

        if (city == locateUnknown) {
            locate()
            return
        }

        db.addCity(city)
        navigator.get()?.jumpToNewCity(city)
    }

    private fun showGpsNotOpen() {
        snackbarObj.set(SnackbarObj(context.getString(R.string.locate_failed),
            context.getString(R.string.goto_open),
            View.OnClickListener {
                navigator.get()?.jumpToOpenGps()
            }))
    }

    private fun setLocation(city: String) = location.set(city)

    private inner class MyLocationListener : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation) {
            locationClient.stop()
            setLocation(location.city ?: locateUnknown)
        }
    }
}