package com.jy.weather.activity

import android.content.Intent
import android.os.Bundle
import com.jy.weather.JYApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 启动界面
 *
 * Created by liu on 2018/1/8.
 */
class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        AndroidSchedulers.mainThread().scheduleDirect({ jumpActivity() }, 1, TimeUnit.SECONDS)
    }

    private fun jumpActivity() {
        val city = JYApplication.cityDB.defaultCity
        if (city == null) {
            startActivity(Intent(this, ChooseCityActivity::class.java))
        } else {
            startActivity(Intent(this, WeatherActivity::class.java).apply {
                putExtra("cityId", city)
            })
        }
        finish()
    }
}
