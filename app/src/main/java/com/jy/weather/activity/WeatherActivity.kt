package com.jy.weather.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.jy.weather.BR
import com.jy.weather.R
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.databinding.ActivityWeatherBinding
import com.jy.weather.navigator.WeatherNavigator
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.dialog_lifestyle.view.*

class WeatherActivity : BaseActivity(), WeatherNavigator {

    private lateinit var binding: ActivityWeatherBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback
    private lateinit var dialogText: TextView

    private val lifestyleDialog: Dialog by lazy {
        createDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather)
        viewModel = WeatherViewModel()
        binding.viewModel = viewModel

        setupListener()

        setupRecyclerView()

        setupSnackbarCallback()

        viewModel.onNewIntent(intent.getStringExtra("city"))
    }

    private fun setupListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.onRefreshListener()
        }
    }

    private fun setupRecyclerView() {
        // hourly_forecast
        binding.rvHourly.apply {
            layoutManager = LinearLayoutManager(this@WeatherActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
            adapter = CommonAdapter(
                viewModel.hourlyForecasts,
                R.layout.item_hourly_forecast,
                BR.hourlyForecast
            )
        }

        // daily_forecast
        binding.rvDaily.apply {
            layoutManager = object : LinearLayoutManager(this@WeatherActivity,
                LinearLayoutManager.VERTICAL, false) {
                // 屏蔽RecyclerView的垂直滑动，否则与最外层ScrollView冲突，导致滑动卡顿
                override fun canScrollVertically(): Boolean = false
            }
            setHasFixedSize(true)
            adapter = CommonAdapter(
                viewModel.dailyForecasts,
                R.layout.item_daily_forecast,
                BR.dailyForecast
            )
        }
    }

    private fun setupSnackbarCallback() {
        snackbarCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val snackbarObj = viewModel.snackbarObj.get() ?: return
                SnackbarUtil.showSnackBar(
                    window.decorView,
                    snackbarObj.text,
                    snackbarObj.action,
                    snackbarObj.listener
                )
            }
        }
        viewModel.snackbarObj.addOnPropertyChangedCallback(snackbarCallback)
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        viewModel.onNewIntent(intent.getStringExtra("city"))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false) // 返回桌面而不是退出应用
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun showLifestyleDialog() = lifestyleDialog.show()

    @SuppressLint("InflateParams")
    private fun createDialog() =
        Dialog(this, R.style.SimpleDialogTheme).apply {
            addContentView(
                layoutInflater.inflate(R.layout.dialog_lifestyle, null).apply {
                    dialogText = tv_message
                },
                getDialogParams(0.65)
            )
            setOnShowListener {
                dialogText.text = viewModel.dialogText
            }
        }

    override fun jumpToTodayActivity() =
        startActivity(Intent(this, TodayActivity::class.java).apply {
            putExtra("city", viewModel.currentCity.get())
            putExtra("update_time", viewModel.updateTime.get())
            putExtra("now", viewModel.now)
            putExtra("daily_forecast", viewModel.dailyForecasts[0])
        })

    override fun jumpToCityManageActivity() =
        startActivity(Intent(this, CityManageActivity::class.java))

    override fun jumpToSettingActivity() =
        startActivity(Intent(this, SettingActivity::class.java))

    override fun startAutoUpdateService() {
        startService(Intent(this, AutoUpdateService::class.java))
    }

    override fun onDataRefresh() {
        // 刷新数据动画
        ValueAnimator.ofInt(binding.svScroll.height, 0).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                val currentValue = valueAnimator.animatedValue as Int
                binding.svScroll.smoothScrollTo(0, currentValue)
            }
            start()
        }
    }
}