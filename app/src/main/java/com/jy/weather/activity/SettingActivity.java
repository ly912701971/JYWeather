package com.jy.weather.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.adapter.IntervalTimeAdapter;
import com.jy.weather.databinding.ActivitySettingBinding;
import com.jy.weather.service.AutoUpdateService;
import com.jy.weather.util.DrawableUtil;
import com.jy.weather.util.NotificationUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ActivitySettingBinding binding;

    private boolean hasClearCache;
    private boolean hasChangeInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        binding.sNotification.setOnCheckedChangeListener(this);
        binding.sAutoUpdate.setOnCheckedChangeListener(this);
        binding.llUpdateInterval.setOnClickListener(this);
        binding.llClearCache.setOnClickListener(this);

        hasClearCache = false;
        hasChangeInterval = false;

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> exitActivity());

        String spCode = JYApplication.cityDB.getCondCode();
        if (spCode != null) {
            binding.rlSettingBackground.setBackgroundResource(DrawableUtil.INSTANCE.getBackground(spCode));
        }

        binding.sNotification.setChecked(JYApplication.cityDB.getNotification());
        binding.sAutoUpdate.setChecked(JYApplication.cityDB.getAutoUpdate());
        changeUpdateColor(JYApplication.cityDB.getAutoUpdate());
        binding.tvIntervalTime.setText(String.valueOf(JYApplication
                .cityDB.getUpdateInterval()).concat(" 小时"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_clear_cache:// 点击清除缓存
                openClearCacheDialog();
                break;

            case R.id.ll_update_interval:// 点击设置自动更新时间
                openUpdateIntervalDialog();
                break;

            default:
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.s_notification:// 通知栏开关
                if (isChecked) {
                    NotificationUtil.INSTANCE.openNotification(this);
                } else {
                    NotificationUtil.INSTANCE.cancelNotification(this);
                }
                JYApplication.cityDB.setNotification(isChecked);
                break;

            case R.id.s_auto_update:// 自动更新开关:
                JYApplication.cityDB.setAutoUpdate(isChecked);
                changeUpdateColor(isChecked);
                break;

            default:
                break;
        }
    }

    private void changeUpdateColor(boolean autoUpdate) {
        if (autoUpdate) {
            binding.tvUpdateInterval.setTextColor(getResources().getColor(R.color.white));
            binding.tvIntervalTime.setTextColor(getResources().getColor(R.color.white));
            binding.ivUpdateIcon.setImageResource(R.drawable.ic_goto);
            binding.llUpdateInterval.setClickable(true);
        } else {
            binding.tvUpdateInterval.setTextColor(getResources().getColor(R.color.text_gray));
            binding.tvIntervalTime.setTextColor(getResources().getColor(R.color.text_gray));
            binding.ivUpdateIcon.setImageResource(R.drawable.ic_goto_gray);
            binding.llUpdateInterval.setClickable(false);
        }
    }

    private void openUpdateIntervalDialog() {
        final Dialog updateTimeDialog = new Dialog(this, R.style.SimpleDialogTheme);
        View updateTimeView = getLayoutInflater().inflate(R.layout.dialog_update_interval, null);

        final int[] hours = new int[]{1, 2, 4, 8, 12};
        final String[] intervalTimes = getResources().getStringArray(R.array.interval_time);
        ListView lvIntervalTime = updateTimeView.findViewById(R.id.lv_interval_time);
        lvIntervalTime.setAdapter(new IntervalTimeAdapter(this, intervalTimes));
        lvIntervalTime.setOnItemClickListener((adapterView, view, i, l) -> {
            hasChangeInterval = true;
            binding.tvIntervalTime.setText(intervalTimes[i]);
            JYApplication.cityDB.setUpdateInterval(hours[i]);
            updateTimeDialog.dismiss();
        });
        updateTimeView.findViewById(R.id.tv_cancel).setOnClickListener(view -> updateTimeDialog.dismiss());

        updateTimeDialog.setContentView(updateTimeView, getDialogParams(8));
        updateTimeDialog.setCancelable(false);
        updateTimeDialog.show();
    }

    private void openClearCacheDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("确认清除缓存?")
                .setPositiveButton("确认", (dialogInterface, i) -> {
                    JYApplication.cityDB.clearCache();
                    hasClearCache = true;
                    showSnackBar(binding.llUpdateInterval, "清除缓存成功");
                    dialogInterface.dismiss();
                })
                .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss()).setCancelable(false);
        builder.create().show();
    }

    private void exitActivity() {
        if (hasClearCache) {
            startActivity(new Intent(SettingActivity.this, ChooseCityActivity.class));
        }
        if (binding.sAutoUpdate.isChecked() && hasChangeInterval) {
            startService(new Intent(SettingActivity.this, AutoUpdateService.class));
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitActivity();
    }
}
