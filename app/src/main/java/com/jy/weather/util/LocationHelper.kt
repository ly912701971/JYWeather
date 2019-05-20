package com.jy.weather.util

import android.content.Context
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption

class LocationHelper(context: Context) {

    private val client: LocationClient = LocationClient(context).apply {
        locOption = LocationClientOption().apply {
            setIsNeedAddress(true)
            isOpenGps = true
            timeOut = 4000
        }
    }

    public fun locate(listener: (String?) -> Unit) {
        client.apply {
            registerLocationListener(object : BDAbstractLocationListener() {
                override fun onReceiveLocation(location: BDLocation?) {
                    client.stop()
                    listener(location?.city)
                }
            })
            start()
        }
    }
}