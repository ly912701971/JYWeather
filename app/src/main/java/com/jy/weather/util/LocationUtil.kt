package com.jy.weather.util

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.jy.weather.JYApplication

object LocationUtil {

    private val client: LocationClient by lazy {
        init()
    }

    private fun init(): LocationClient =
        LocationClient(JYApplication.INSTANCE).apply {
            locOption = LocationClientOption().apply {
                setIsNeedAddress(true)
                isOpenGps = true
                timeOut = 4000
            }
        }

    fun locate(listener: (BDLocation) -> Unit) {
        client.apply {
            registerLocationListener(object : BDAbstractLocationListener() {
                override fun onReceiveLocation(location: BDLocation?) {
                    client.stop()
                    listener(location ?: return)
                }
            })
            start()
        }
    }
}