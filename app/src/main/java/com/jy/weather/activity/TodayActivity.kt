package com.jy.weather.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jy.weather.R
import com.jy.weather.databinding.ActivityTodayBinding
import com.jy.weather.entity.DailyForecast
import com.jy.weather.entity.Now
import com.jy.weather.viewmodel.TodayViewModel

/**
 * 今日详情界面
 *
 * Created by Yang on 2017/12/11.
 */
class TodayActivity : BaseActivity() {

    private lateinit var binding: ActivityTodayBinding
    private lateinit var viewModel: TodayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_today)
        viewModel = TodayViewModel()
        binding.viewModel = viewModel

        setupToolbar()

        val intent = intent
        viewModel.start(
            intent.getStringExtra("city") ?: "",
            intent.getStringExtra("update_time") ?: "",
            intent.getSerializableExtra("now") as Now,
            intent.getSerializableExtra("daily_forecast") as DailyForecast
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
