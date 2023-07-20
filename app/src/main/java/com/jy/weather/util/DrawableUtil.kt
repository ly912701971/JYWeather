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
            150 -> R.drawable.ic_150
            151 -> R.drawable.ic_151
            152 -> R.drawable.ic_152
            153 -> R.drawable.ic_153
            300 -> R.drawable.ic_300
            301 -> R.drawable.ic_301
            302 -> R.drawable.ic_302
            303 -> R.drawable.ic_303
            304 -> R.drawable.ic_304
            305 -> R.drawable.ic_305
            306 -> R.drawable.ic_306
            307 -> R.drawable.ic_307
            308 -> R.drawable.ic_308
            309 -> R.drawable.ic_309
            310 -> R.drawable.ic_310
            311 -> R.drawable.ic_311
            312 -> R.drawable.ic_312
            313 -> R.drawable.ic_313
            314 -> R.drawable.ic_314
            315 -> R.drawable.ic_315
            316 -> R.drawable.ic_316
            317 -> R.drawable.ic_317
            318 -> R.drawable.ic_318
            350 -> R.drawable.ic_350
            351 -> R.drawable.ic_351
            399 -> R.drawable.ic_399
            400 -> R.drawable.ic_400
            401 -> R.drawable.ic_401
            402 -> R.drawable.ic_402
            403 -> R.drawable.ic_403
            404 -> R.drawable.ic_404
            405 -> R.drawable.ic_405
            406 -> R.drawable.ic_406
            407 -> R.drawable.ic_407
            408 -> R.drawable.ic_408
            409 -> R.drawable.ic_409
            410 -> R.drawable.ic_410
            456 -> R.drawable.ic_456
            457 -> R.drawable.ic_457
            499 -> R.drawable.ic_499
            500 -> R.drawable.ic_500
            501 -> R.drawable.ic_501
            502 -> R.drawable.ic_502
            503 -> R.drawable.ic_503
            504 -> R.drawable.ic_504
            507 -> R.drawable.ic_507
            508 -> R.drawable.ic_508
            509 -> R.drawable.ic_509
            510 -> R.drawable.ic_510
            511 -> R.drawable.ic_511
            512 -> R.drawable.ic_512
            513 -> R.drawable.ic_513
            514 -> R.drawable.ic_514
            515 -> R.drawable.ic_515
            800 -> R.drawable.ic_800
            801 -> R.drawable.ic_801
            802 -> R.drawable.ic_802
            803 -> R.drawable.ic_803
            804 -> R.drawable.ic_804
            805 -> R.drawable.ic_805
            806 -> R.drawable.ic_806
            807 -> R.drawable.ic_807
            900 -> R.drawable.ic_900
            901 -> R.drawable.ic_901
            else -> R.drawable.ic_999
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
                when (Integer.parseInt(condCode) / 100) {
                    1 -> R.drawable.ic_background_cloud
                    2 -> R.drawable.ic_background_wind
                    3 -> R.drawable.ic_background_rain
                    4 -> R.drawable.ic_background_snow
                    5 -> R.drawable.ic_background_haze
                    else -> R.drawable.ic_background_cloud // 默认为多云天气
                }
            }
        }

    fun getIndexIconId(type: String) = when (type) {
        "1" -> R.drawable.ic_sport
        "2" -> R.drawable.ic_wash_car
        "3" -> R.drawable.ic_dress
        "5" -> R.drawable.ic_uv
        "6" -> R.drawable.ic_travel
        "8" -> R.drawable.ic_comfort
        "9" -> R.drawable.ic_flu
        "10" -> R.drawable.ic_air
        else -> 0
    }
}