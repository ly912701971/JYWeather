package com.jy.weather.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.CityListAdapter
import com.jy.weather.databinding.ActivityCityManageBinding
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.SnackbarUtil
import java.util.*

class CityManageActivity : BaseActivity() {

    private lateinit var binding: ActivityCityManageBinding
    private lateinit var adapter: CityListAdapter

    private var citySet = mutableSetOf<String>()
    private var cityList = mutableListOf<Map<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_manage)

        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val spCode = JYApplication.cityDB.condCode
        if (spCode != null) {
            binding.rlCityManageBackground.setBackgroundResource(DrawableUtil.getBackground(spCode))
        }

        citySet = HashSet(JYApplication.cityDB.citySet)
        var weatherData: String?
        var cityData: MutableMap<String, String>
        for (city in citySet) {
            weatherData = JYApplication.cityDB.getData(city)
            val weather = JsonUtil.handleWeatherResponse(weatherData)
            if (weather != null) {
                cityData = HashMap()
                cityData["city"] = weather.basic.cityName
                cityData["cond_code"] = weather.now.code
                cityData["admin_area"] = weather.basic.parentCity + " - " + weather.basic.adminArea
                cityData["temp_scope"] = weather.dailyForecasts[0].getMinTemp() + " ~ " + weather.dailyForecasts[0].getMaxTemp() + "C"
                cityList.add(cityData)
            }
        }
        adapter = CityListAdapter(this, cityList)
        binding.smlvCityList.adapter = adapter

        // SwipeMenuListView构造器
        binding.smlvCityList.setMenuCreator { menu ->
            // "常驻"item
            val defaultItem = SwipeMenuItem(this@CityManageActivity)
            defaultItem.title = "常驻"
            defaultItem.titleSize = 16
            defaultItem.setBackground(R.color.item_default)
            defaultItem.titleColor = Color.WHITE
            defaultItem.width = 240
            menu.addMenuItem(defaultItem)

            // "删除城市"item
            val deleteItem = SwipeMenuItem(this@CityManageActivity)
            deleteItem.title = "删除"
            deleteItem.titleSize = 16
            deleteItem.setBackground(R.color.item_delete)
            deleteItem.titleColor = Color.WHITE
            deleteItem.width = 240
            menu.addMenuItem(deleteItem)
        }
        // SwipeMenuListView子项点击事件
        binding.smlvCityList.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@CityManageActivity, WeatherActivity::class.java)
            intent.putExtra("city", cityList[position]["city"])
            startActivity(intent)
        }
        // SwipeMenuListView向左滑动
        binding.smlvCityList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT)
        // SwipeMenuListView子项菜单点击事件
        binding.smlvCityList.setOnMenuItemClickListener { position, _, index ->
            val city = cityList[position]["city"] ?: return@setOnMenuItemClickListener false
            val defaultCity = JYApplication.cityDB.defaultCity
            when (index) {
                // 点击"常驻"
                0 -> if (city != defaultCity) {
                    JYApplication.cityDB.defaultCity = city
                    adapter.notifyDataSetChanged()
                }
                // 点击"删除"
                1 -> if (citySet.size == 1) {// 只有一个城市
                    SnackbarUtil.showSnackBar(binding.smlvCityList, "请至少保留一个城市")
                } else if (city == defaultCity) {// 删除常驻城市
                    SnackbarUtil.showSnackBar(binding.smlvCityList, "您不能删除常驻城市")
                } else {
                    for (data in cityList) {
                        if (city == data["city"]) {
                            cityList.remove(data)
                            adapter.notifyDataSetChanged()
                            break
                        }
                    }
                    citySet.remove(city)
                    JYApplication.cityDB.citySet = citySet
                    JYApplication.cityDB.removeCity(city)
                }
            }
            false// false:关闭菜单
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_city_manage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_choose_city) {
            startActivity(Intent(this, ChooseCityActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
