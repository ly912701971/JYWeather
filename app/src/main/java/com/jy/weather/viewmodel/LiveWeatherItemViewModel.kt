package com.jy.weather.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.jy.weather.entity.LiveWeather
import com.jy.weather.navigator.LiveWeatherNavigator
import java.lang.ref.WeakReference

class LiveWeatherItemViewModel(liveWeather: LiveWeather, navigator: LiveWeatherNavigator) {

    private val navigator = WeakReference(navigator)

    val userName: ObservableField<String> = ObservableField(liveWeather.userName)
    val userPortrait: ObservableField<String> = ObservableField(liveWeather.userPortrait)
    val liveTime: ObservableField<String> = ObservableField(liveWeather.liveTime)
    val liveText: ObservableField<String> = ObservableField(liveWeather.liveText)
    val location: ObservableField<String> = ObservableField(liveWeather.location)
    val liveUrl: ObservableField<String> = ObservableField(liveWeather.liveUrl)
    val liveTextVisibility: ObservableBoolean = ObservableBoolean(liveWeather.liveText.isNotEmpty())

    fun onImageClick() {
        navigator.get()?.showBigImageDialog(liveUrl.get()!!)
    }

    fun onCommentClick() {
        navigator.get()?.showCommmentsSoftInput()
    }
}