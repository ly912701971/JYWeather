package com.jy.weather.util

import com.jy.weather.R

/**
 * 图标工具类
 *
 * Created by Yang on 2017/12/13.
 */
object DrawableUtil {

    /**
     * 获取天气状况代码对应的图标
     */
    @JvmStatic
    fun getCondIcon(condCode: String) =
        when (Integer.parseInt(condCode)) {
            100 -> R.drawable.ic_100
            101 -> R.drawable.ic_101
            102 -> R.drawable.ic_102
            103 -> R.drawable.ic_103
            104 -> R.drawable.ic_104
            200, 202, 203, 204 -> R.drawable.ic_200
            201 -> R.drawable.ic_201
            205, 206, 207 -> R.drawable.ic_205
            208, 209, 210, 211, 212, 213 -> R.drawable.ic_208
            300 -> R.drawable.ic_300
            301 -> R.drawable.ic_301
            302 -> R.drawable.ic_302
            303 -> R.drawable.ic_303
            304 -> R.drawable.ic_304
            305 -> R.drawable.ic_305
            306 -> R.drawable.ic_306
            307 -> R.drawable.ic_307
            308, 312 -> R.drawable.ic_308
            309 -> R.drawable.ic_309
            310 -> R.drawable.ic_310
            311 -> R.drawable.ic_311
            313 -> R.drawable.ic_313
            400 -> R.drawable.ic_400
            401 -> R.drawable.ic_401
            402 -> R.drawable.ic_402
            403 -> R.drawable.ic_403
            404 -> R.drawable.ic_404
            405 -> R.drawable.ic_405
            406 -> R.drawable.ic_406
            407 -> R.drawable.ic_407
            500 -> R.drawable.ic_500
            501 -> R.drawable.ic_501
            502 -> R.drawable.ic_502
            503 -> R.drawable.ic_503
            504 -> R.drawable.ic_504
            507 -> R.drawable.ic_507
            508 -> R.drawable.ic_508
            900 -> R.drawable.ic_900
            901 -> R.drawable.ic_901
            else -> R.drawable.ic_900
        }

    /**
     * 根据天气代码返回天气图片id
     */
    fun getBackground(condCode: String) =
        when (condCode) {
            "100", "900" -> R.drawable.ic_background_sun
            "104" -> R.drawable.ic_background_yin
            "901" -> R.drawable.ic_background_snow
            else -> {
                val code = Integer.parseInt(condCode) / 100
                when (code) {
                    1 -> R.drawable.ic_background_cloud
                    2 -> R.drawable.ic_background_wind
                    3 -> R.drawable.ic_background_rain
                    4 -> R.drawable.ic_background_snow
                    5 -> R.drawable.ic_background_haze
                    else -> R.drawable.ic_background_cloud// 默认为多云天气
                }
            }
        }

    fun getLifeStyleIconId(type: String): Int =
        when (type) {
            "air" -> R.drawable.ic_air
            "comf" -> R.drawable.ic_comfort
            "cw" -> R.drawable.ic_wash_car
            "drsg" -> R.drawable.ic_dress
            "flu" -> R.drawable.ic_flu
            "sport" -> R.drawable.ic_sport
            "trav" -> R.drawable.ic_travel
            "uv" -> R.drawable.ic_uv
            else -> 0
        }
}