package com.example.jy.jyweather.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.IntervalTimeAdapter;
import com.example.jy.jyweather.databinding.ActivitySettingBinding;
import com.example.jy.jyweather.service.AutoUpdateService;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.NotificationUtil;

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
        binding.llAboutUs.setOnClickListener(this);
        binding.sNotification.setOnCheckedChangeListener(this);
        binding.sAutoUpdate.setOnCheckedChangeListener(this);
        binding.llUpdateInterval.setOnClickListener(this);
        binding.llClearCache.setOnClickListener(this);

        hasClearCache = false;
        hasChangeInterval = false;

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });

        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        if (spCode != null) {
            binding.rlSettingBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }

        binding.sNotification.setChecked(JYApplication.getInstance().getCityDB().getNotification());
        binding.sAutoUpdate.setChecked(JYApplication.getInstance().getCityDB().getAutoUpdate());
        changeUpdateColor(JYApplication.getInstance().getCityDB().getAutoUpdate());
        binding.tvIntervalTime.setText(String.valueOf(JYApplication
                .getInstance().getCityDB().getUpdateInterval()).concat(" 小时"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about_us:// 点击关于我们
                startActivity(new Intent(this, AboutUsActivity.class));
                break;

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
                    NotificationUtil.openNotification(this);
                } else {
                    NotificationUtil.cancelNotification(this);
                }
                JYApplication.getInstance().getCityDB().setNotification(isChecked);
                break;

            case R.id.s_auto_update:// 自动更新开关:
                JYApplication.getInstance().getCityDB().setAutoUpdate(isChecked);
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
        lvIntervalTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hasChangeInterval = true;
                binding.tvIntervalTime.setText(intervalTimes[i]);
                JYApplication.getInstance().getCityDB().setUpdateInterval(hours[i]);
                updateTimeDialog.dismiss();
            }
        });
        updateTimeView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTimeDialog.dismiss();
            }
        });

        updateTimeDialog.setContentView(updateTimeView, getDialogParams(8));
        updateTimeDialog.setCancelable(false);
        updateTimeDialog.show();
    }

    private void openClearCacheDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("确认清除缓存?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JYApplication.getInstance().getCityDB().clearCache();
                        hasClearCache = true;
                        showSnackBar(binding.llUpdateInterval, "清除缓存成功");
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false);
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
