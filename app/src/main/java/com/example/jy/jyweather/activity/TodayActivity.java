package com.example.jy.jyweather.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.databinding.ActivityTodayBinding;
import com.example.jy.jyweather.entity.DailyForecastBean;
import com.example.jy.jyweather.entity.NowBean;
import com.example.jy.jyweather.util.DrawableUtil;

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
        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        if (spCode != null) {
            binding.rlTodayBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
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
