package com.jy.weather.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.databinding.ActivityTodayBinding
import com.jy.weather.entity.DailyForecastBean
import com.jy.weather.entity.NowBean
import com.jy.weather.util.DrawableUtil

/**
 * 今日详情界面
 * Created by Yang on 2017/12/11.
 */
class TodayActivity : BaseActivity() {

    private lateinit var binding: ActivityTodayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_today)

        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        val spCode = JYApplication.cityDB.condCode
        if (spCode != null) {
            binding.rlTodayBackground.setBackgroundResource(DrawableUtil.getBackground(spCode))
        }
        val intent = intent
        if (intent != null) {
            binding.location = intent.getStringExtra("location")
            binding.updateTime = intent.getStringExtra("update_time")
            binding.now = intent.getSerializableExtra("now") as NowBean
            binding.dailyForecast = intent.getSerializableExtra("daily_forecast") as DailyForecastBean
        }
    }
}
