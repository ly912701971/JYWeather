package com.jy.weather.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.jy.weather.R
import com.jy.weather.adapter.CityListAdapter
import com.jy.weather.databinding.ActivityCityManageBinding
import com.jy.weather.navigator.CityManageNavigator
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.CityManageViewModel

class CityManageActivity : BaseActivity(), CityManageNavigator {

    private lateinit var binding: ActivityCityManageBinding
    private lateinit var viewModel: CityManageViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_manage)
        viewModel = CityManageViewModel()
        binding.viewModel = viewModel

        setupToolbar()

        setupSwipeMenuListView()

        setupSnackbarCallback()

        viewModel.initData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSwipeMenuListView() {
        binding.smlvCityList.adapter = CityListAdapter(this)

        // SwipeMenuListView构造器
        binding.smlvCityList.setMenuCreator { menu ->
            // "常驻"item
            menu.addMenuItem(SwipeMenuItem(this@CityManageActivity).apply {
                title = "常驻"
                titleSize = 16
                setBackground(R.color.item_default)
                titleColor = Color.WHITE
                width = 240
            })

            // "删除城市"item
            menu.addMenuItem(SwipeMenuItem(this@CityManageActivity).apply {
                title = "删除"
                titleSize = 16
                setBackground(R.color.item_delete)
                titleColor = Color.WHITE
                width = 240
            })
        }

        // SwipeMenuListView向左滑动
        binding.smlvCityList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT)

        // SwipeMenuListView子项点击事件
        binding.smlvCityList.onItemClickListener = AdapterView.OnItemClickListener { _, _, index, _ ->
            viewModel.onItemClick(index)
        }

        // SwipeMenuListView子项菜单点击事件
        binding.smlvCityList.setOnMenuItemClickListener { index, _, position ->
            viewModel.onMenuItemClick(index, position)
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

    override fun jumpToCity(city: String) =
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra("city", city)
        })
}
