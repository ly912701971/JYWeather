package com.jy.weather.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.databinding.ActivityTodayBinding;
import com.jy.weather.entity.DailyForecastBean;
import com.jy.weather.entity.NowBean;
import com.jy.weather.util.DrawableUtil;

/**
 * 今日详情界面
 * Created by Yang on 2017/12/11.
 */
public class TodayActivity extends BaseActivity {

    ActivityTodayBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_today);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        String spCode = JYApplication.cityDB.getCondCode();
        if (spCode != null) {
            binding.rlTodayBackground.setBackgroundResource(DrawableUtil.INSTANCE.getBackground(spCode));
        }
        Intent intent = getIntent();
        if (intent != null) {
            binding.setLocation(intent.getStringExtra("location"));
            binding.setUpdateTime(intent.getStringExtra("update_time"));
            binding.setNow((NowBean) intent.getSerializableExtra("now"));
            binding.setDailyForecast((DailyForecastBean) intent.getSerializableExtra("daily_forecast"));
        }
    }
}
