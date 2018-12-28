package com.example.jy.jyweather.entity;

import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 天气预报实体类
 * <p>
 * Created by Yang on 2017/10/15.
 */
public class DailyForecastBean implements Serializable {

    private String date;            //日期

    @SerializedName("hum")
    private String relativuHum;     //相对湿度

    @SerializedName("pcpn")
    private String precipitation;   //降水量

    @SerializedName("pop")
    private String probability;     //降水概率

    @SerializedName("pres")
    private String airPressure;     //气压

    @SerializedName("uv_index")
    private String ultraviolet;     //紫外线

    @SerializedName("vis")
    private String visibility;      //能见度

    @SerializedName("cond_code_d")
    private String dayCondCode;     //白天天气状况代码

    @SerializedName("cond_code_n")
    private String nightCondCode;   //晚上天气状况代码

    @SerializedName("cond_txt_d")
    private String dayCondText;     //白天天气状况描述

    @SerializedName("cond_txt_n")
    private String nightCondText;   //晚上天气状况描述

    @SerializedName("sr")
    private String sunRise;     //日升时间

    @SerializedName("ss")
    private String sunSet;      //日落时间

    @SerializedName("mr")
    private String moonRise;    //月升时间

    @SerializedName("ms")
    private String moonSet;     //月落时间

    @SerializedName("tmp_max")
    private String maxTemp;     //最大温度

    @SerializedName("tmp_min")
    private String minTemp;     //最低温度

    @SerializedName("wind_deg")
    private String windDegree;      //风向(度数)

    @SerializedName("wind_dir")
    private String windDirection;   //风向(方位)

    @SerializedName("wind_sc")
    private String windScale;       //风力等级

    @SerializedName("wind_spd")
    private String windSpeed;       //风速

    private String weekday;

    private int icon;

    public String getDate() {
        return date.substring(date.indexOf("-") + 1, date.length());
    }

    public String getRelativuHum() {
        return relativuHum;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getProbability() {
        return probability;
    }

    public String getAirPressure() {
        return airPressure;
    }

    public String getUltraviolet() {
        return ultraviolet;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getDayCondCode() {
        return dayCondCode;
    }

    public String getNightCondCode() {
        return nightCondCode;
    }

    public String getDayCondText() {
        return dayCondText;
    }

    public String getNightCondText() {
        return nightCondText;
    }

    public String getSunRise() {
        return sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public String getMoonRise() {
        return moonRise;
    }

    public String getMoonSet() {
        return moonSet;
    }

    public String getMaxTemp() {
        return maxTemp.concat("°");
    }

    public String getMinTemp() {
        return minTemp.concat("°");
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

    public String getWeekday() {
        return StringUtil.getWeekday(date);
    }

    public int getIcon() {
        return DrawableUtil.getCondIcon(dayCondCode);
    }
}
