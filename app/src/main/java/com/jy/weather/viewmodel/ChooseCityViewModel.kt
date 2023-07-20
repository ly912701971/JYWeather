package com.jy.weather.viewmodel

import android.text.Editable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.lifecycleScope
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.Location
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GpsUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.LocationUtil
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseCityViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private var hasGranted: Boolean = false
    private val locateUnknown: String by lazy { context.getString(R.string.locate_unknown) }
    private val locList = mutableListOf<Location>()

    private lateinit var navigator: WeakReference<ChooseCityNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val location: ObservableField<String> = ObservableField(context.getString(R.string.locating))
    val searchResult: ObservableList<String> = ObservableArrayList()
    val hasSearch: ObservableBoolean = ObservableBoolean(false)
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: ChooseCityNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun onPermissionGranted() {
        hasGranted = true
        locate()
    }

    fun afterTextChanged(editable: Editable) {
        if (editable.isNotEmpty()) {
            navigator.get()?.getActivity()?.lifecycleScope?.launch(Dispatchers.IO) {
                NetworkInterface.queryLocationData(editable.toString())?.let {
                    searchResult.clear()
                    locList.clear()
                    JsonUtil.handleLocationData(it)?.data?.forEach { loc ->
                        searchResult.add(loc.loc)
                        locList.add(loc)
                    }
                }
                hasSearch.set(true)
            }
        } else {
            hasSearch.set(false)
        }
    }

    fun locate() {
        if (GpsUtil.isOpen(context)) {
            LocationUtil.locate {
                setLocation(it.city ?: locateUnknown)
            }
        } else {
            setLocation(locateUnknown)
            showGpsNotOpen()
        }
    }

    fun onPermissionDenied() = setLocation(locateUnknown)

    fun onCityClick(city: String, cityId: String) {
        if (!NetworkUtil.isNetworkAvailable()) {
            snackbarObj.set(SnackbarObj(context.getString(R.string.network_unavailable)))
            return
        }

        if (!hasGranted) {
            snackbarObj.set(SnackbarObj(context.getString(R.string.permission_denied)))
            return
        } else if (!GpsUtil.isOpen(context)) {
            showGpsNotOpen()
            return
        }

        val cityName = if (cityId.isEmpty()) location.get() else city.replace(Regex("[市县区]"), "")
        if (cityName == locateUnknown) return
        navigator.get()?.startWeatherActivity(cityName, cityId)
    }

    fun onSearchResultItemClick(index: Int) =
        navigator.get()
            ?.startWeatherActivity(locList[index].name, locList[index].id, locList[index])

    private fun showGpsNotOpen() = snackbarObj.set(SnackbarObj(
        context.getString(R.string.locate_failed), context.getString(R.string.goto_open)
    ) {
        navigator.get()?.startOpenGpsActivity()
    })

    private fun setLocation(city: String) = location.set(city)
}