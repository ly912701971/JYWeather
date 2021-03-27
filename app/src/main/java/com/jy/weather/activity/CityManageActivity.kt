package com.jy.weather.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.jy.weather.R
import com.jy.weather.adapter.CityListAdapter
import com.jy.weather.databinding.ActivityCityManageBinding
import com.jy.weather.navigator.CityManageNavigator
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.PermissionUtil
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.CityManageViewModel

class CityManageActivity : BaseActivity(), CityManageNavigator {

    companion object {
        private const val PERMISSION = android.Manifest.permission.SEND_SMS
    }

    private lateinit var binding: ActivityCityManageBinding
    private lateinit var viewModel: CityManageViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback

    private var index = 0
    private val manager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_manage)
        viewModel = CityManageViewModel()
        binding.viewModel = viewModel

        setupView()

        setupSnackbarCallback()

        viewModel.initData()
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.smlvCityList.adapter = CityListAdapter(this)

        // SwipeMenuListView构造器
        binding.smlvCityList.setMenuCreator { menu ->
            // "常驻"item
            menu.addMenuItem(SwipeMenuItem(this).apply {
                title = getString(R.string.resident)
                titleSize = 16
                setBackground(R.color.item_default)
                titleColor = Color.WHITE
                width = 240
            })

            menu.addMenuItem(SwipeMenuItem(this).apply {
                title = getString(R.string.family_number)
                titleSize = 16
                setBackground(R.color.orange)
                titleColor = Color.WHITE
                width = 280
            })

            // "删除城市"item
            menu.addMenuItem(SwipeMenuItem(this).apply {
                title = getString(R.string.delete)
                titleSize = 16
                setBackground(R.color.item_delete)
                titleColor = Color.WHITE
                width = 240
            })
        }

        // SwipeMenuListView向左滑动
        binding.smlvCityList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT)

        // SwipeMenuListView子项点击事件
        binding.smlvCityList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
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

    override fun requestPermission(index: Int) {
        this.index = index
        PermissionUtil.checkPermissionAndRequest(
            this,
            PERMISSION,
            {
                showFamilyNumberDialog(index)
            },
            {
                showPermissionHintDialog()
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionUtil.onPermissionResult(
            grantResults,
            {
                showFamilyNumberDialog(index)
            }
        )
    }

    override fun startWeatherActivity(city: String) =
        startActivity(Intent(this, WeatherActivity::class.java).apply {
            putExtra("city", city)
        })

    @SuppressLint("InflateParams")
    private fun showFamilyNumberDialog(index: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_family_number, null)
        val etCall = view.findViewById<EditText>(R.id.et_call).apply {
            isCursorVisible = false
            setOnClickListener {
                isCursorVisible = true
            }
        }
        val etNumber = view.findViewById<EditText>(R.id.et_number)
        viewModel.cities[index].apply {
            if (phoneNumber.isNotEmpty()) {
                etCall.setText(call)
                etNumber.setText(phoneNumber)
            }
        }

        val dialog = AlertDialog.Builder(this, AlertDialogUtil.getTheme())
            .setTitle(R.string.family_number)
            .setView(view)
            .setPositiveButton(R.string.confirm, null)
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.clear, null)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val call = etCall.text.toString()
                val number = etNumber.text.toString()
                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                viewModel.dealFamilyNumber(index, call, number, dialog)
            }

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                etCall.setText("")
                etNumber.setText("")
            }
        }

        dialog.show()
    }

    private fun showPermissionHintDialog() =
        AlertDialogUtil.showDialog(
            this,
            getString(R.string.send_message_permission),
            {
                PermissionUtil.requestPermission(this, PERMISSION)
            }
        )
}
