package com.example.jy.jyweather.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by liyang
 * on 2019/1/4
 */
public class GpsUtil {

    /**
     * 判断定位是否打开
     */
    public static boolean isOpen(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }
}
