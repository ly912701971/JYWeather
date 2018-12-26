package com.example.jy.jyweather.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 和风天气实体类
 * <p>
 * Created by Yang on 2017/10/15.
 */
public class WeatherBean {

    private String status;

    private UpdateBean update;

    private BasicBean basic;

    private NowBean now;

    @SerializedName("daily_forecast")
    private List<DailyForecastBean> dailyForecasts;

    @SerializedName("hourly")
    private List<HourlyForecastBean> hourlyForecasts;

    @SerializedName("lifestyle")
    private List<LifestyleBean> lifestyles;

    public String getStatus() {
        return status;
    }

    public UpdateBean getUpdate() {
        return update;
    }

    public BasicBean getBasic() {
        return basic;
    }

    public NowBean getNow() {
        return now;
    }

    public List<DailyForecastBean> getDailyForecasts() {
        return dailyForecasts;
    }

    public List<HourlyForecastBean> getHourlyForecasts() {
        return hourlyForecasts;
    }

    public List<LifestyleBean> getLifestyles() {
        return lifestyles;
    }
}
