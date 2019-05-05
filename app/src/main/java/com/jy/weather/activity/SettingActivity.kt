package com.jy.weather.activity

import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import com.jy.weather.R
import com.jy.weather.databinding.ActivitySettingBinding
import com.jy.weather.navigator.SettingNavigator
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.SettingViewModel

class SettingActivity : BaseActivity(), SettingNavigator {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        viewModel = SettingViewModel()
        binding.viewModel = viewModel

        setupToolbar()

        setupSnackbarCallback()
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
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

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun startChooseCityActivity() {
        startActivity(Intent(this, ChooseCityActivity::class.java))
    }

    override fun startAutoUpdateService() {
        startService(Intent(this, AutoUpdateService::class.java))
    }

    override fun showIntervalDialog() {
        AlertDialog.Builder(this, AlertDialogUtil.getTheme())
            .setTitle(getString(R.string.update_interval))
            .setSingleChoiceItems(viewModel.intervalTimes, viewModel.getChosenIndex()) { dialog, index ->
                viewModel.onIntervalItemChoose(index)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun showClearCacheDialog() {
        AlertDialogUtil.showDialog(this,
            getString(R.string.confirm_clear_cache),
            {
                viewModel.onClearCacheClick()
            })
    }

    override fun finish() {
        viewModel.finish()
        super.finish()
    }
}
