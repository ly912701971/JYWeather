package com.jy.weather.activity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.CitySearchAdapter
import com.jy.weather.databinding.ActivityChooseCityBinding
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GpsUtil
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class ChooseCityActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, View.OnClickListener {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private var locationCity: String? = null
    private lateinit var cityList: List<String>
    private var searchResult = mutableListOf<String>()

    lateinit var binding: ActivityChooseCityBinding

    lateinit var mClient: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_city)
        binding.clickListener = this

        if (!EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "您需要同意以下权限方可使用定位功能", 1, *PERMISSIONS)
        } else {
            locate()
        }
        init()
    }

    private fun init() {
        val spCode = JYApplication.cityDB.condCode
        if (spCode != null) {
            binding.rlChooseBackground.setBackgroundResource(DrawableUtil.getBackground(spCode))
        }

        binding.ivBack.setOnClickListener {
            when {
                binding.etSearch.text.isNotEmpty() -> binding.etSearch.setText("")
                JYApplication.cityDB.defaultCity == null -> {
                    addCity("北京")
                    finish()
                }
                else -> finish()
            }
        }

        // 添加全国城市列表
        cityList = Arrays.asList(*resources.getStringArray(R.array.national_cities_list))
        val adapter = CitySearchAdapter(this@ChooseCityActivity, searchResult)
        binding.lvSearchResult.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty()) {// 有输入时查询匹配结果
                    searchResult.clear()
                    for (cityInfo in cityList) {
                        if (cityInfo.contains(editable)) {
                            searchResult.add(cityInfo)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    binding.llSearchResult.visibility = View.VISIBLE
                    binding.glHotCity.visibility = View.GONE
                } else {
                    binding.glHotCity.visibility = View.VISIBLE
                    binding.llSearchResult.visibility = View.GONE
                }
            }
        })
    }

    private fun locate() {
        if (GpsUtil.isOpen(JYApplication.INSTANCE)) {
            // 百度地图自动定位
            mClient = LocationClient(JYApplication.INSTANCE)
            mClient.registerLocationListener(MyLocationListener())
            val option = LocationClientOption()
            option.setIsNeedAddress(true)
            option.isOpenGps = true
            mClient.locOption = option
            mClient.start()
        } else {
            onLocationFailed()
            showGpsNotOpen()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (perms.size == PERMISSIONS.size) {
            locate()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        onLocationFailed()
        showPermissionDenied()
    }

    private fun addCity(city: String) {
        val citySet = HashSet(JYApplication.cityDB.citySet)
        citySet.add(city)
        JYApplication.cityDB.citySet = citySet
        if (citySet.size == 1) {
            JYApplication.cityDB.defaultCity = city
        }

        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra("city", city)
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View) {
        if (!isNetworkAvailable) {
            showSnackBar(v, getString(R.string.network_unavailable))
            return
        }

        var city = ""
        when (v.id) {
            R.id.tv_location -> if (!GpsUtil.isOpen(JYApplication.INSTANCE)) {
                showGpsNotOpen()
                return
            } else {
                if (locationCity != null) {
                    city = locationCity as String
                }
            }

            else -> city = (v as TextView).text.toString()
        }

        addCity(city.replace("市", ""))
    }

    private fun showGpsNotOpen() {
        showSnackBar(binding.tvLocation,
            resources.getString(R.string.locate_failed),
            "前往打开",
            View.OnClickListener {
                //            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                //            startActivityForResult(intent, 2);
                val intent = Intent("com.jy.weather")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
                intent.component = comp
                startActivity(intent)
            })
    }

    private fun showPermissionDenied() {
        showSnackBar(binding.tvLocation,
            resources.getString(R.string.permission_denied),
            "前往打开",
            View.OnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 1)
            })
    }

    private fun onLocationSuccessful(city: String) {
        binding.tvLocation.text = city
        locationCity = city.replace("市", "")
            .replace("区", "")
            .replace("县", "")
    }

    private fun onLocationFailed() {
        binding.tvLocation.text = resources.getString(R.string.locate_unknown)
    }

    private inner class MyLocationListener : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation) {
            mClient.stop()

            val city = location.city
            if (city == null) {
                onLocationFailed()
            } else {
                onLocationSuccessful(city)
            }
        }
    }
}
