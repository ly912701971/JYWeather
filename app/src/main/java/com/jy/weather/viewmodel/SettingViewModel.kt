package com.jy.weather.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.widget.CompoundButton
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.navigator.SettingNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.NotificationUtil
import com.jy.weather.util.SnackbarObj
import java.lang.ref.WeakReference

class SettingViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private var hasClearCache = false
    private var hasChangeInterval = false

    private lateinit var navigator: WeakReference<SettingNavigator>

    val bgResId: ObservableField<Int> = ObservableField(DrawableUtil.getBackground(db.condCode))
    val notificationCheck: ObservableBoolean = ObservableBoolean(db.notification)
    val autoUpdateCheck: ObservableBoolean = ObservableBoolean(db.autoUpdate)
    val intervalText: ObservableField<String> = ObservableField("${db.updateInterval} 小时")
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()

    val colorWhite = context.resources.getColor(R.color.white)
    val colorGray = context.resources.getColor(R.color.text_gray)
    val gotoIcon = R.drawable.ic_goto
    val gotoIconGray = R.drawable.ic_goto_gray
    val intervalTimes: Array<String> by lazy {
        context.resources.getStringArray(R.array.interval_time)
    }

    fun start(navigator: SettingNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun onNotificationCheck(view: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            NotificationUtil.openNotification(context)
        } else {
            NotificationUtil.cancelNotification(context)
        }

        notificationCheck.set(isChecked)
        db.notification = isChecked
    }

    fun onAutoUpdateCheck(view: CompoundButton, isChecked: Boolean) {
        autoUpdateCheck.set(isChecked)
        db.autoUpdate = isChecked

        navigator.get()?.startAutoUpdateService()
    }

    fun showIntervalDialog() = navigator.get()?.showIntervalDialog()

    fun getChosenIndex(): Int = intervalTimes.indexOf(intervalText.get())

    fun onIntervalItemChoose(index: Int) {
        val time = intervalTimes[index]
        intervalText.set(time)
        db.updateInterval = time.split(Regex(" "))[0].toInt()

        hasChangeInterval = true
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
        if (autoUpdateCheck.get() && hasChangeInterval) {
            navigator.get()?.startAutoUpdateService()
        }
    }
}