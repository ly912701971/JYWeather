package com.example.jy.jyweather.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 伪城市数据库
 * 利用SharedPreferences实现
 * <p>
 * Created by Yang on 2018/1/9.
 */
public class CityDB {

    private final String DB_BASE_PATH = "city_database";

    private final String DB_CITY_SET = "city_set";

    private final String DB_DEFAULT_CITY = "default_city";

    private final String DB_COND_CODE = "cond_code";

    private final String DB_NOTIFICATION = "notification";

    private final String DB_AUTO_UPDATE = "auto_update";

    private final String DB_UPDATE_INTERVAL = "update_interval";

    private SharedPreferences sp;

    private Editor editor;

    public CityDB(Context context) {
        sp = context.getSharedPreferences(DB_BASE_PATH, Context.MODE_PRIVATE);
    }

    public Set<String> getCitySet() {
        return sp.getStringSet(DB_CITY_SET, new LinkedHashSet<String>());
    }

    public String getDefaultCity() {
        return sp.getString(DB_DEFAULT_CITY, null);
    }

    public String getCondCode() {
        return sp.getString(DB_COND_CODE, null);
    }

    public String getData(String key) {
        return sp.getString(key, null);
    }

    public boolean getNotification() {
        // 默认不打开通知栏
        return sp.getBoolean(DB_NOTIFICATION, false);
    }

    public boolean getAutoUpdate() {
        // 默认不自动更新
        return sp.getBoolean(DB_AUTO_UPDATE, false);
    }

    public int getUpdateInterval() {
        // 默认更新时间为2小时
        return sp.getInt(DB_UPDATE_INTERVAL, 2);
    }

    public void setCitySet(Set<String> citySet) {
        editor = sp.edit();
        editor.putStringSet(DB_CITY_SET, citySet);
        editor.apply();
    }

    public void setDefaultCity(String defaultCity) {
        editor = sp.edit();
        editor.putString(DB_DEFAULT_CITY, defaultCity);
        editor.apply();
    }

    public void setCondCode(String condCode) {
        editor = sp.edit();
        editor.putString(DB_COND_CODE, condCode);
        editor.apply();
    }

    public void setCityData(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setNotification(boolean notification) {
        editor = sp.edit();
        editor.putBoolean(DB_NOTIFICATION, notification);
        editor.apply();
    }

    public void setAutoUpdate(boolean autoUpdate) {
        editor = sp.edit();
        editor.putBoolean(DB_AUTO_UPDATE, autoUpdate);
        editor.apply();
    }

    public void removeCity(String city) {
        editor = sp.edit();
        editor.remove(city);
        editor.apply();
    }

    public void clearCache() {
        editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public void setUpdateInterval(int hour) {
        editor = sp.edit();
        editor.putInt(DB_UPDATE_INTERVAL, hour);
        editor.apply();
    }
}
