package com.jy.weather.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.recyclerview.widget.GridLayoutManager
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.ChooseCityAdapter
import com.jy.weather.adapter.CitySearchAdapter
import com.jy.weather.databinding.ActivityChooseCityBinding
import com.jy.weather.entity.Location
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.PermissionUtil
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.ChooseCityViewModel

class ChooseCityActivity : BaseActivity(), ChooseCityNavigator {

    companion object {
        private const val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val LOCATION_REQUEST_CODE = 0
    }

    private lateinit var binding: ActivityChooseCityBinding
    private lateinit var viewModel: ChooseCityViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback
    private val hotCities = listOf(
        "未知" to "",
        "北京市" to "101010100",
        "天津市" to "101030100",
        "上海市" to "101020100",
        "重庆市" to "101040100",
        "沈阳市" to "101070101",
        "大连市" to "101070201",
        "长春市" to "101060101",
        "哈尔滨市" to "101050101",
        "郑州市" to "101180101",
        "武汉市" to "101200101",
        "长沙市" to "101250101",
        "广州市" to "101280101",
        "深圳市" to "101280601",
        "南京市" to "101190101",
        "杭州市" to "101210101",
        "东莞市" to "101281601",
        "成都市" to "101270101",
        "青岛市" to "101120201",
        "苏州市" to "101190401",
        "厦门市" to "101230201"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_city)
        viewModel = ChooseCityViewModel()
        binding.viewModel = viewModel

        setupListener()

        setupSnackbarCallback()

        requestPermission()
    }

    private fun setupListener() {
        // 返回图标点击事件
        binding.ivBack.setOnClickListener {
            when {
                binding.etSearch.text.isNotEmpty() -> binding.etSearch.setText("")
                JYApplication.cityDB.defaultCity == null -> {// 默认城市北京
                    startWeatherActivity(hotCities[1].first, hotCities[1].second)
                    finish()
                }

                else -> finish()
            }
        }

        binding.rvHotCity.also {
            it.layoutManager = GridLayoutManager(this, 3)
            it.adapter = ChooseCityAdapter(hotCities) { city, cityId ->
                viewModel.onCityClick(city, cityId)
            }
        }

        binding.lvSearchResult.also {
            it.adapter = CitySearchAdapter(this)
            it.setOnItemClickListener { _, _, index, _ ->
                viewModel.onSearchResultItemClick(index)
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

    private fun requestPermission() =
        PermissionUtil.checkPermissionAndRequest(
            this,
            PERMISSION,
            {
                viewModel.onPermissionGranted()
            },
            {
                showPermissionHintDialog()
            }
        )

    private fun showPermissionHintDialog() =
        AlertDialogUtil.showDialog(
            this,
            resources.getString(R.string.request_location_permission),
            {
                PermissionUtil.requestPermission(this, PERMISSION)
            },
            {
                viewModel.onPermissionDenied()
            }
        )

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionUtil.onPermissionResult(
            grantResults,
            {
                viewModel.onPermissionGranted()
            },
            {
                viewModel.onPermissionDenied()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE) {
            viewModel.locate()
        }
    }

    override fun getActivity() = this

    override fun startOpenGpsActivity() =
        startActivityForResult(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
            LOCATION_REQUEST_CODE
        )

    override fun startWeatherActivity(city: String?, cityId: String, location: Location?) {
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra("city", city)
            putExtra("cityId", cityId)
            if (location != null) {
                putExtra("location", location)
            }
        })
        finish()
    }
}
