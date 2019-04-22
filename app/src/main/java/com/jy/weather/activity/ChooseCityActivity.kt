package com.jy.weather.activity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.CitySearchAdapter
import com.jy.weather.databinding.ActivityChooseCityBinding
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.ChooseCityViewModel
import pub.devrel.easypermissions.EasyPermissions

class ChooseCityActivity : BaseActivity(),
    EasyPermissions.PermissionCallbacks,
    ChooseCityNavigator {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var binding: ActivityChooseCityBinding
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback
    private lateinit var viewModel: ChooseCityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_city)

        viewModel = ChooseCityViewModel()
        viewModel.setNavigator(this)
        binding.viewModel = viewModel

        if (!EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "您需要同意以下权限方可使用定位功能", 1, *PERMISSIONS)
        } else {
            viewModel.locate()
        }
        init()
    }

    private fun init() {
        // 返回图标点击事件
        binding.ivBack.setOnClickListener {
            when {
                binding.etSearch.text.isNotEmpty() -> binding.etSearch.setText("")
                JYApplication.cityDB.defaultCity == null -> {
                    viewModel.addCity("北京")
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
        binding.lvSearchResult.adapter = CitySearchAdapter(this, this)

        // snackbar
        snackbarCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val snackbarObj = viewModel.snackbarObj.get() ?: return
                SnackbarUtil.showSnackBar(window.decorView,
                    snackbarObj.text,
                    snackbarObj.action,
                    snackbarObj.listener)
            }
        }
        viewModel.snackbarObj.addOnPropertyChangedCallback(snackbarCallback)
    }

    override fun onResume() {
        super.onResume()

        viewModel.start()
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (perms.size == PERMISSIONS.size) {
            viewModel.locate()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        viewModel.setLocation(viewModel.locateUnknown)
        showPermissionDenied()
    }

    private fun showPermissionDenied() {
        SnackbarUtil.showSnackBar(window.decorView,
            resources.getString(R.string.permission_denied),
            resources.getString(R.string.goto_open),
            View.OnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 1)
            })
    }

    override fun jumpToOpenGps() {
        //            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //            startActivityForResult(intent, 2);
        val intent = Intent("com.jy.weather")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        intent.component = comp
        startActivity(intent)
    }

    override fun jumpToNewCity(city: String) {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra("city", city)
        startActivity(intent)
        finish()
    }
}
