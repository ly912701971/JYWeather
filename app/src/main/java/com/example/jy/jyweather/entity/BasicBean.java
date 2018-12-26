package com.example.jy.jyweather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Basic实体类
 * <p>
 * Created by Yang on 2017/10/15.
 */
public class BasicBean {

    @SerializedName("cid")
    private String cityId;      //城市id

    @SerializedName("location")
    private String cityName;    //城市名称

    @SerializedName("parent_city")
    private String parentCity;  //上级城市

    @SerializedName("admin_area")
    private String adminArea;   //所属行政区

    @SerializedName("cnty")
    private String country;     //国家

    @SerializedName("lon")
    private String longitude;   //经度

    @SerializedName("lat")
    private String latitude;    //纬度

    @SerializedName("tz")
    private String timeZone;    //时区

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getParentCity() {
        return parentCity;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public String getCountry() {
        return country;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
