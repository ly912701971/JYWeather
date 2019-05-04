package com.jy.weather.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.jy.weather.JYApplication

/**
 * 启动界面
 *
 * Created by liu on 2018/1/8.
 */
class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()

        Handler().postDelayed(
            {
                jumpActivity()
            },
            1000
        )
    }

    private fun jumpActivity() {
        val city = JYApplication.cityDB.defaultCity
        if (city == null) {
            startActivity(Intent(this, ChooseCityActivity::class.java))
        } else {
            startActivity(Intent(this, WeatherActivity::class.java).apply {
                putExtra("city", city)
            })
        }
        finish()
    }
}
