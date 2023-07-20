package com.jy.weather.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by liyang on 2023/7/20
 * email: liyang4@yy.com
 */
class LocationData : DataWrapper() {
    @SerializedName("location")
    val data: List<Location>? = null
}

class Location : Serializable {
    val adm1: String = ""
    val adm2: String = ""
    val country: String = ""
    val fxLink: String = ""
    val id: String = ""
    val isDst: String = ""
    val lat: String = ""
    val lon: String = ""
    val name: String = ""
    val rank: String = ""
    val type: String = ""
    val tz: String = ""
    val utcOffset: String = ""
    val loc get() = "$name - $adm2 - $adm1"
}