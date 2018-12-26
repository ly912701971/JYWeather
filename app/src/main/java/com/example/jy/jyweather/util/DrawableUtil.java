package com.example.jy.jyweather.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.jy.jyweather.R;

/**
 * 图标工具类
 * <p>
 * Created by Yang on 2017/12/13.
 */
public class DrawableUtil {

    /**
     * 获取天气状况代码对应的图标
     */
    public static int getCondIcon(String condCode) {
        int code = Integer.parseInt(condCode);
        switch (code) {
            case 100:
                return R.drawable.ic_100;

            case 101:
                return R.drawable.ic_101;

            case 102:
                return R.drawable.ic_102;

            case 103:
                return R.drawable.ic_103;

            case 104:
                return R.drawable.ic_104;

            case 200:
            case 202:
            case 203:
            case 204:
                return R.drawable.ic_200;

            case 201:
                return R.drawable.ic_201;

            case 205:
            case 206:
            case 207:
                return R.drawable.ic_205;

            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
                return R.drawable.ic_208;

            case 300:
                return R.drawable.ic_300;

            case 301:
                return R.drawable.ic_301;

            case 302:
                return R.drawable.ic_302;

            case 303:
                return R.drawable.ic_303;

            case 304:
                return R.drawable.ic_304;

            case 305:
                return R.drawable.ic_305;

            case 306:
                return R.drawable.ic_306;

            case 307:
                return R.drawable.ic_307;

            case 308:
            case 312:
                return R.drawable.ic_308;

            case 309:
                return R.drawable.ic_309;

            case 310:
                return R.drawable.ic_310;

            case 311:
                return R.drawable.ic_311;

            case 313:
                return R.drawable.ic_313;

            case 400:
                return R.drawable.ic_400;

            case 401:
                return R.drawable.ic_401;

            case 402:
                return R.drawable.ic_402;

            case 403:
                return R.drawable.ic_403;

            case 404:
                return R.drawable.ic_404;

            case 405:
                return R.drawable.ic_405;

            case 406:
                return R.drawable.ic_406;

            case 407:
                return R.drawable.ic_407;

            case 500:
                return R.drawable.ic_500;

            case 501:
                return R.drawable.ic_501;

            case 502:
                return R.drawable.ic_502;

            case 503:
                return R.drawable.ic_503;

            case 504:
                return R.drawable.ic_504;

            case 507:
                return R.drawable.ic_507;

            case 508:
                return R.drawable.ic_508;

            case 900:
                return R.drawable.ic_900;

            case 901:
                return R.drawable.ic_901;

            default:
                return R.drawable.ic_900;
        }
    }

    /**
     * 根据天气代码返回天气图片id
     */
    public static int getBackground(String condCode) {
        if (condCode.equals("100") || condCode.equals("900")) {
            return R.drawable.ic_background_sun;
        } else if (condCode.equals("104")) {
            return R.drawable.ic_background_yin;
        } else if (condCode.equals("901")) {
            return R.drawable.ic_background_snow;
        } else {
            int code = Integer.parseInt(condCode) / 100;
            switch (code) {
                case 1:
                    return R.drawable.ic_background_cloud;

                case 2:
                    return R.drawable.ic_background_wind;

                case 3:
                    return R.drawable.ic_background_rain;

                case 4:
                    return R.drawable.ic_background_snow;

                case 5:
                    return R.drawable.ic_background_haze;

                default:
            }
        }
        return R.drawable.ic_background_cloud;// 默认为多云天气
    }

}
