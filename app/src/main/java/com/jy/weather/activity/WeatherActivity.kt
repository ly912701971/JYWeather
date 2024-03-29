package com.jy.weather.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jy.weather.BR
import com.jy.weather.R
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.databinding.ActivityWeatherBinding
import com.jy.weather.entity.Location
import com.jy.weather.navigator.WeatherNavigator
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
        viewModel.start(this)
        binding.viewModel = viewModel

        setupView()

        setupSnackbarCallback()

        viewModel.onNewIntent(
            intent.getStringExtra("city") ?: "",
            intent.getStringExtra("cityId") ?: "",
            intent.getSerializableExtra("location") as? Location
        )
    }

    private fun setupView() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.onRefreshListener()
        }

        // hourly_forecast
        binding.rvHourly.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
            adapter = CommonAdapter(
                viewModel.hourly,
                R.layout.item_hourly_forecast,
                BR.hourlyForecast
            )
        }

        // daily_forecast
        binding.rvDaily.apply {
            layoutManager = object : LinearLayoutManager(
                context,
                VERTICAL,
                false
            ) {
                override fun isAutoMeasureEnabled(): Boolean = true

                // 屏蔽RecyclerView的垂直滑动，否则与最外层ScrollView冲突，导致滑动卡顿
                override fun canScrollVertically(): Boolean = false
            }
            setHasFixedSize(true)
            adapter = CommonAdapter(
                viewModel.daily,
                R.layout.item_daily_forecast,
                BR.dailyForecast
            )
        }

        binding.rvLife.run {
            layoutManager = GridLayoutManager(context, 4)
            setHasFixedSize(true)
            adapter = CommonAdapter(
                viewModel.index,
                R.layout.item_lift_style,
                BR.style
            ).apply {
                setOnItemClickListener {
                    viewModel.showLifestyleDialog(it)
                }
            }
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
        viewModel.onNewIntent(
            intent.getStringExtra("city") ?: "",
            intent.getStringExtra("cityId") ?: "",
            intent.getSerializableExtra("location") as? Location
        )
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

    override fun getActivity() = this

    override fun startTodayActivity() =
        startActivity(Intent(this, TodayActivity::class.java).apply {
            putExtra("city", viewModel.currentCity.get())
            putExtra("now", viewModel.weather.get()?.now)
            putExtra("daily", viewModel.daily[0])
        })

    override fun startCityManageActivity() =
        startActivity(Intent(this, CityManageActivity::class.java))

    override fun startSettingActivity() =
        startActivity(Intent(this, SettingActivity::class.java))

    override fun startLiveWeatherActivity() =
        startActivity(Intent(this, LiveWeatherActivity::class.java))

    override fun startDataRefreshAnimator() {
        // 刷新数据动画
        runOnUiThread {
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
}