package com.example.jy.jyweather.util;

import com.example.jy.jyweather.entity.WeatherBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 解析和处理JSON数据工具类
 *
 * Created by Yang on 2017/10/21.
 */

public class JsonUtil {

    public static WeatherBean handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, WeatherBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
