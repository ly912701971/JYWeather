package com.jy.weather.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.jy.weather.BR
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.databinding.*
import com.jy.weather.entity.*
import com.jy.weather.network.NetworkInterface
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.NotificationUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class WeatherActivity : BaseActivity(), View.OnClickListener {

    private lateinit var basic: Basic
    private lateinit var now: Now
    private lateinit var update: Update
    private lateinit var dailyForecasts: List<DailyForecast>
    private lateinit var hourlyForecasts: List<HourlyForecast>
    private lateinit var lifestyles: List<Lifestyle>

    private val handler = MyHandler()
    private lateinit var mCity: String

    private lateinit var weatherBinding: ActivityWeatherBinding
    private lateinit var headerBinding: WeatherHeaderBinding
    private lateinit var nowBinding: WeatherNowBinding
    private lateinit var hourlyBinding: WeatherHourlyForecastBinding
    private lateinit var dailyBinding: WeatherDailyForecastBinding
    private lateinit var lifeBinding: WeatherLifeSuggestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        weatherBinding = DataBindingUtil.setContentView(this, R.layout.activity_weather)
        headerBinding = weatherBinding.headerPart
        nowBinding = weatherBinding.nowPart
        hourlyBinding = weatherBinding.hourlyForecastPart
        dailyBinding = weatherBinding.dailyForecastPart
        lifeBinding = weatherBinding.lifeSuggestionPart
        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        headerBinding.ivHome.setOnClickListener(this)
        headerBinding.ivSetting.setOnClickListener(this)
        headerBinding.tvCity.setOnClickListener(this)
        nowBinding.llBrief.setOnClickListener(this)
        nowBinding.tvNowTemp.setOnClickListener(this)

        mCity = intent.getStringExtra("city")
        getData(mCity)

        weatherBinding.srlRefresh.setOnRefreshListener {
            if (!isNetworkAvailable) {
                showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable))
            } else {
                weatherBinding.srlRefresh.isRefreshing = true
                getData(mCity)
            }
        }

        if (JYApplication.cityDB.autoUpdate) {
            startService(Intent(this, AutoUpdateService::class.java))
        }
    }

    /**
     * 获取数据
     */
    private fun getData(city: String) {
        weatherBinding.srlRefresh.isRefreshing = true

        if (!isNetworkAvailable) {
            weatherBinding.srlRefresh.isRefreshing = false
            showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable))
            handleData(JYApplication.cityDB.getData(city))
            return
        }

        NetworkInterface.queryWeatherData(city, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()

                runOnUiThread {
                    showSnackBar(weatherBinding.srlRefresh, getString(R.string.data_unavailable))
                    weatherBinding.srlRefresh.isRefreshing = false
                }

                val dataText = JYApplication.cityDB.getData(city)
                handleData(dataText)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body() ?: return
                val responseString = body.string()
                JYApplication.cityDB.setCityData(mCity, responseString)
                handleData(responseString)
            }
        })
    }

    private fun handleData(dataText: String?) {
        val weather = JsonUtil.handleWeatherResponse(dataText) ?: return
        if (weather.status == "ok") {
            basic = weather.basic
            now = weather.now
            update = weather.update
            dailyForecasts = weather.dailyForecasts
            hourlyForecasts = weather.hourlyForecasts
            lifestyles = weather.lifestyles

            // 发送回调成功消息
            val message = handler.obtainMessage()
            message.what = 0
            message.sendToTarget()
        }
    }

    /**
     * 初始化布局
     */
    private fun initView() {
        if (JYApplication.cityDB.notification) {
            NotificationUtil.openNotification(this)
        }
        JYApplication.cityDB.condCode = now.code

        headerBinding.basic = basic
        nowBinding.now = now
        nowBinding.updateTime = update.getLoc()
        weatherBinding.llMainBackground.setBackgroundResource(now.getNowBackground())

        // hourly_forecast
        val hourlyManager = LinearLayoutManager(this)
        hourlyManager.orientation = LinearLayoutManager.HORIZONTAL
        hourlyBinding.rvHourly.layoutManager = hourlyManager
        hourlyBinding.rvHourly.setHasFixedSize(true)
        hourlyBinding.rvHourly.adapter = CommonAdapter(hourlyForecasts,
            R.layout.item_hourly_forecast,
            BR.hourlyForecast)

        // daily_forecast
        val dailyManager = object : LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                // 屏蔽RecyclerView的垂直滑动，否则与最外层ScrollView冲突，导致滑动卡顿
                return false
            }
        }
        dailyBinding.rvDaily.layoutManager = dailyManager
        dailyBinding.rvDaily.setHasFixedSize(true)
        dailyBinding.rvDaily.adapter = CommonAdapter(dailyForecasts,
            R.layout.item_daily_forecast,
            BR.dailyForecast)

        // lifestyle
        if (!lifestyles.isEmpty()) {
            for ((type, brief, text) in lifestyles) {
                when (type) {
                    "comf" -> {
                        lifeBinding.tvComfort.text = brief
                        lifeBinding.llComfort.setOnClickListener { createDialog(text).show() }
                    }

                    "cw" -> {
                        lifeBinding.tvCarWash.text = brief
                        lifeBinding.llCarWash.setOnClickListener { createDialog(text).show() }
                    }

                    "drsg" -> {
                        lifeBinding.tvDress.text = brief
                        lifeBinding.llDress.setOnClickListener { createDialog(text).show() }
                    }

                    "flu" -> {
                        lifeBinding.tvFlu.text = brief
                        lifeBinding.llFlu.setOnClickListener { createDialog(text).show() }
                    }

                    "sport" -> {
                        lifeBinding.tvSport.text = brief
                        lifeBinding.llSport.setOnClickListener { createDialog(text).show() }
                    }

                    "trav" -> {
                        lifeBinding.tvTravel.text = brief
                        lifeBinding.llTravel.setOnClickListener { createDialog(text).show() }
                    }

                    "uv" -> {
                        lifeBinding.tvUv.text = brief
                        lifeBinding.llUv.setOnClickListener { createDialog(text).show() }
                    }

                    "air" -> {
                        lifeBinding.tvAir.text = brief
                        lifeBinding.llAir.setOnClickListener { createDialog(text).show() }
                    }
                }
            }
        }
        changeVisibility(View.VISIBLE)

        // 刷新数据动画
        val animator = ValueAnimator.ofInt(weatherBinding.svScroll.height, 0)
        animator.duration = 1000
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            weatherBinding.svScroll.smoothScrollTo(0, currentValue)
        }
        animator.start()

        weatherBinding.srlRefresh.isRefreshing = false
    }

    private fun createDialog(message: String): Dialog {
        val dialog = Dialog(this, R.style.SimpleDialogTheme)
        val view = layoutInflater.inflate(R.layout.dialog_lifestyle, null)
        (view.findViewById<View>(R.id.tv_message) as TextView).text = message
        dialog.addContentView(view, getDialogParams(6))
        return dialog
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_now_temp, R.id.ll_brief -> {
                val intent = Intent(this, TodayActivity::class.java)
                intent.putExtra("location", basic.cityName)
                intent.putExtra("update_time", update.getLoc())
                intent.putExtra("now", now)
                intent.putExtra("daily_forecast", dailyForecasts[0])
                startActivity(intent)
            }

            R.id.iv_home, R.id.tv_city -> startActivity(Intent(this, CityManageActivity::class.java))

            R.id.iv_setting -> startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        super.onNewIntent(intent)

        val city = intent.getStringExtra("city")
        if (mCity == city) {
            return
        } else {
            mCity = city
        }

        changeVisibility(View.INVISIBLE)
        getData(mCity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                mCity = data.getStringExtra("city")
                getData(mCity)
            }
        }
    }

    private fun changeVisibility(visibility: Int) {
        headerBinding.rlHeader.visibility = visibility
        nowBinding.rlNow.visibility = visibility
        hourlyBinding.llHourlyForecast.visibility = visibility
        dailyBinding.llDailyForecast.visibility = visibility
        lifeBinding.llLifeSuggestion.visibility = visibility
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 返回桌面而不是退出应用
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("HandlerLeak")
    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> initView()
            }
        }
    }
}