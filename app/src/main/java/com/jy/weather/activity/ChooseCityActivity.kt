package com.jy.weather.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.CitySearchAdapter
import com.jy.weather.databinding.ActivityChooseCityBinding
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.util.AlertDialogUtil
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
                    startWeatherActivity(resources.getString(R.string.default_city))
                    finish()
                }
                else -> finish()
            }
        }

        // 搜索输入监听
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable) {
                viewModel.afterTextChanged(editable)
            }
        })

        // adapter
        binding.lvSearchResult.adapter = CitySearchAdapter(this)
        binding.lvSearchResult.setOnItemClickListener { _, _, index, _ ->
            viewModel.onSearchResultItemClick(index)
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

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                showPermissionDialog()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(PERMISSION), 0)
            }
        } else {
            viewModel.hasGranted = true
            viewModel.locate()
        }
    }

    private fun showPermissionDialog() {
        AlertDialogUtil.showDialog(this,
            resources.getString(R.string.request_permission),
            {
                ActivityCompat.requestPermissions(this, arrayOf(PERMISSION), 0)
            },
            {
                viewModel.permissionDenied()
            })
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasGranted = true
            viewModel.locate()
        } else {
            viewModel.permissionDenied()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE) {
            viewModel.locate()
        }
    }

    override fun startOpenGpsActivity() =
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Companion.LOCATION_REQUEST_CODE)

    override fun startWeatherActivity(city: String) {
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra("city", city)
        })
        finish()
    }
}
