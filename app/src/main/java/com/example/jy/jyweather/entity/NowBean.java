package com.example.jy.jyweather.entity;

import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Now实体类
 * <p>
 * Created by Yang on 2017/10/15.
 */
public class NowBean implements Serializable {

    @SerializedName("cloud")
    private String cloud;           //云量

    @SerializedName("cond_code")
    private String code;            //天气状况代码

    @SerializedName("cond_txt")
    private String condText;        //天气数据详情

    @SerializedName("fl")
    private String feelTemp;        //体感温度

    @SerializedName("hum")
    private String relativeHum;     //相对湿度

    @SerializedName("pcpn")
    private String precipitation;   //降水量

    @SerializedName("pres")
    private String airPressure;     //气压

    @SerializedName("tmp")
    private String temperature;     //温度

    @SerializedName("vis")
    private String visibility;      //能见度

    @SerializedName("wind_deg")
    private String windDegree;      //风向(度数)

    @SerializedName("wind_dir")
    private String windDirection;   //风向(方位)

    @SerializedName("wind_sc")
    private String windScale;       //风力等级

    @SerializedName("wind_spd")
    private String windSpeed;       //风速

    private int nowIcon;

    private int nowBackground;

    public String getCloud() {
        return cloud;
    }

    public String getCode() {
        return code;
    }

    public String getCondText() {
        return condText;
    }

    public String getFeelTemp() {
        return feelTemp;
    }

    public String getRelativeHum() {
        return "湿度".concat(relativeHum).concat("%");
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getAirPressure() {
        return airPressure;
    }

    public String getTemperature() {
        return temperature.concat("°");
    }

    public String getVisibility() {
        return visibility;
    }

    public String getWindDegree() {
        return windDegree;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindScale() {
        if (StringUtil.hasNumber(windScale)) {
            return windScale.concat("级");
        }
        return windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public int getNowIcon() {
        return DrawableUtil.getCondIcon(code);
    }

    public int getNowBackground() {
        return DrawableUtil.getBackground(code);
    }
}
