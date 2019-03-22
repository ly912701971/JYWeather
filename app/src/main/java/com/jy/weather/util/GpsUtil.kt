package com.jy.weather.util

import android.content.Context
import android.location.LocationManager

/**
 * Gps工具类
 *
 * Created by liyang
 * on 2019/1/4
 */
object GpsUtil {

    /**
     * 判断定位是否打开
     */
    fun isOpen(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }
}
