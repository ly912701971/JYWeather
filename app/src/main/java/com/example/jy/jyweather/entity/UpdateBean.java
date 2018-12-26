package com.example.jy.jyweather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Update实体类
 *
 * Created by Yang on 2017/12/11.
 */
public class UpdateBean {

    @SerializedName("loc")
    private String loc; //当地时间

    @SerializedName("utc")
    private String utc; //UTC时间

    public String getLoc() {
        return loc;
    }

    public String getUtc() {
        return utc;
    }
}
