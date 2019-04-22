package com.jy.weather.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ListView

import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.adapter.IntervalTimeAdapter
import com.jy.weather.databinding.ActivitySettingBinding
import com.jy.weather.service.AutoUpdateService
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.NotificationUtil
import com.jy.weather.util.SnackbarUtil

class SettingActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivitySettingBinding

    private var hasClearCache = false
    private var hasChangeInterval = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTrans()
        setStatusBarColor()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        init()
    }

    /**
     * 初始化
     */
    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.sNotification.setOnCheckedChangeListener(this)
        binding.sAutoUpdate.setOnCheckedChangeListener(this)
        binding.llUpdateInterval.setOnClickListener(this)
        binding.llClearCache.setOnClickListener(this)

        hasClearCache = false
        hasChangeInterval = false

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { exitActivity() }

        val spCode = JYApplication.cityDB.condCode
        if (spCode != null) {
            binding.rlSettingBackground.setBackgroundResource(DrawableUtil.getBackground(spCode))
        }

        binding.sNotification.isChecked = JYApplication.cityDB.notification
        binding.sAutoUpdate.isChecked = JYApplication.cityDB.autoUpdate
        changeUpdateColor(JYApplication.cityDB.autoUpdate)
        binding.tvIntervalTime.text = "${JYApplication.cityDB.updateInterval} 小时"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_clear_cache// 点击清除缓存
            -> openClearCacheDialog()

            R.id.ll_update_interval// 点击设置自动更新时间
            -> openUpdateIntervalDialog()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.s_notification// 通知栏开关
            -> {
                if (isChecked) {
                    NotificationUtil.openNotification(this)
                } else {
                    NotificationUtil.cancelNotification(this)
                }
                JYApplication.cityDB.notification = isChecked
            }

            R.id.s_auto_update// 自动更新开关:
            -> {
                JYApplication.cityDB.autoUpdate = isChecked
                changeUpdateColor(isChecked)
            }

            else -> {
            }
        }
    }

    private fun changeUpdateColor(autoUpdate: Boolean) {
        if (autoUpdate) {
            binding.tvUpdateInterval.setTextColor(getColorById(R.color.white))
            binding.tvIntervalTime.setTextColor(getColorById(R.color.white))
            binding.ivUpdateIcon.setImageResource(R.drawable.ic_goto)
            binding.llUpdateInterval.isClickable = true
        } else {
            binding.tvUpdateInterval.setTextColor(getColorById(R.color.text_gray))
            binding.tvIntervalTime.setTextColor(getColorById(R.color.text_gray))
            binding.ivUpdateIcon.setImageResource(R.drawable.ic_goto_gray)
            binding.llUpdateInterval.isClickable = false
        }
    }

    private fun openUpdateIntervalDialog() {
        val updateTimeDialog = Dialog(this, R.style.SimpleDialogTheme)
        val updateTimeView = layoutInflater.inflate(R.layout.dialog_update_interval, null)

        val hours = intArrayOf(1, 2, 4, 8, 12)
        val intervalTimes = resources.getStringArray(R.array.interval_time)
        val lvIntervalTime = updateTimeView.findViewById<ListView>(R.id.lv_interval_time)
        lvIntervalTime.adapter = IntervalTimeAdapter(this, intervalTimes)
        lvIntervalTime.setOnItemClickListener { _, _, position, _ ->
            hasChangeInterval = true
            binding.tvIntervalTime.text = intervalTimes[position]
            JYApplication.cityDB.updateInterval = hours[position]
            updateTimeDialog.dismiss()
        }
        updateTimeView.findViewById<View>(R.id.tv_cancel).setOnClickListener { updateTimeDialog.dismiss() }

        updateTimeDialog.setContentView(updateTimeView, getDialogParams(0.8))
        updateTimeDialog.setCancelable(false)
        updateTimeDialog.show()
    }

    private fun openClearCacheDialog() {
        val builder = AlertDialog.Builder(this).setMessage("确认清除缓存?")
            .setPositiveButton("确认") { dialogInterface, _ ->
                JYApplication.cityDB.clearCache()
                hasClearCache = true
                SnackbarUtil.showSnackBar(binding.llUpdateInterval, "清除缓存成功")
                dialogInterface.dismiss()
            }
            .setNegativeButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }.setCancelable(false)
        builder.create().show()
    }

    private fun exitActivity() {
        if (hasClearCache) {
            startActivity(Intent(this@SettingActivity, ChooseCityActivity::class.java))
        }
        if (binding.sAutoUpdate.isChecked && hasChangeInterval) {
            startService(Intent(this@SettingActivity, AutoUpdateService::class.java))
        }
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitActivity()
    }
}
