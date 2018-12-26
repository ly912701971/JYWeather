package com.example.jy.jyweather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 未来每小时预报
 *
 * Created by Yang on 2017/10/15.
 */
public class HourlyForecastBean {

    private String time;            //时间

    @SerializedName("hum")
    private String relativuHum;     //相对湿度

    @SerializedName("pop")
    private String probability;     //降水概率

    @SerializedName("dew")
    private String dewTemp;         //露点温度

    @SerializedName("pres")
    private String airPressure;     //气压

    @SerializedName("tmp")
    private String temperature;     //温度

    @SerializedName("wind_deg")
    private String windDegree;      //风向(度数)

    @SerializedName("wind_dir")
    private String windDirection;   //风向(方位)

    @SerializedName("wind_sc")
    private String windScale;       //风力等级

    @SerializedName("wind_spd")
    private String windSpeed;       //风速

    @SerializedName("cloud")
    private String cloud;           //云量

    @SerializedName("cond_code")
    private String code;            //天气状况代码

    @SerializedName("cond_txt")
    private String condText;        //天气数据详情

    public String getTime() {
        return time;
    }

    public String getRelativuHum() {
        return relativuHum;
    }

    public String getProbability() {
        return probability;
    }

    public String getDewTemp() {
        return dewTemp;
    }

    public String getAirPressure() {
        return airPressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindDegree() {
        return windDegree;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindScale() {
        return windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getCloud() {
        return cloud;
    }

    public String getCode() {
        return code;
    }

    public String getCondText() {
        return condText;
    }
}
