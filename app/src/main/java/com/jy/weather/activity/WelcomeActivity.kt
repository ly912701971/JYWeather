package com.jy.weather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.jy.weather.JYApplication
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * 启动界面
 *
 * Created by liu on 2018/1/8.
 */
class WelcomeActivity : BaseActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()

        Observable.timer(1, TimeUnit.SECONDS)
            .subscribe {
                jumpActivity()
            }
    }

    private fun jumpActivity() {
        val city = JYApplication.cityDB.defaultCity
        if (city == null) {
            startActivity(Intent(this, ChooseCityActivity::class.java))
        } else {
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("city", city)
            startActivity(intent)
        }
        finish()
    }
}
