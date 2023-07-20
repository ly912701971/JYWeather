package com.jy.weather.viewmodel

import androidx.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.navigator.SettingNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference

class SettingViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private var hasClearCache = false

    private lateinit var navigator: WeakReference<SettingNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    fun start(navigator: SettingNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun showClearCacheDialog() = navigator.get()?.showClearCacheDialog()

    fun onClearCacheClick() {
        db.clearCache()
        hasClearCache = true
        snackbarObj.set(SnackbarObj(context.getString(R.string.clear_cache_success)))
    }

    fun finish() {
        if (hasClearCache) {
            navigator.get()?.startChooseCityActivity()
        }
    }
}