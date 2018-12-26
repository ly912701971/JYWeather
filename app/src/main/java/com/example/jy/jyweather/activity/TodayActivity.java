package com.example.jy.jyweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.entity.DailyForecastBean;
import com.example.jy.jyweather.entity.NowBean;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 今日详情界面
 * Created by Yang on 2017/12/11.
 */
public class TodayActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.iv_weather_icon)
    ImageView ivWeatherIcon;
    @BindView(R.id.tv_now_temp)
    TextView tvNowTemp;
    @BindView(R.id.tv_fl)
    TextView tvFl;
    @BindView(R.id.tv_hum)
    TextView tvHum;
    @BindView(R.id.tv_wind_sc)
    TextView tvWindSc;
    @BindView(R.id.tv_wind_dir)
    TextView tvWindDir;
    @BindView(R.id.tv_vis)
    TextView tvVis;
    @BindView(R.id.tv_uv)
    TextView tvUv;
    @BindView(R.id.tv_pres)
    TextView tvPres;
    @BindView(R.id.tv_sun_rise)
    TextView tvSunRise;
    @BindView(R.id.tv_sun_set)
    TextView tvSunSet;
    @BindView(R.id.rl_today_background)
    RelativeLayout rlTodayBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        setContentView(R.layout.activity_today);
        ButterKnife.bind(this);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        if (spCode != null) {
            rlTodayBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }
        Intent intent = getIntent();
        String location = null;
        String updateTime = null;
        NowBean now = null;
        DailyForecastBean dailyForecast = null;
        if (intent != null) {
            location = intent.getStringExtra("location");
            updateTime = intent.getStringExtra("update_time");
            now = (NowBean) intent.getSerializableExtra("now");
            dailyForecast = (DailyForecastBean) intent.getSerializableExtra("daily_forecast");
        }

        toolbar.setTitle(location);
        tvUpdateTime.setText(updateTime);

        if (now != null) {
            ivWeatherIcon.setImageResource(DrawableUtil.getCondIcon(now.getCode()));
            tvNowTemp.setText(now.getTemperature().concat(getString(R.string.degree)));
            tvFl.setText(now.getFeelTemp().concat(getString(R.string.degree)));
            tvHum.setText(now.getRelativeHum().concat(getString(R.string.percent)));
            if (StringUtil.hasNumber(now.getWindScale())) {
                tvWindSc.setText(now.getWindScale().concat(getString(R.string.level)));
            } else {
                tvWindSc.setText(now.getWindScale());
            }
            tvWindDir.setText(now.getWindDirection());
            tvVis.setText(now.getVisibility().concat(getString(R.string.km)));
        }

        if (dailyForecast != null) {
            tvUv.setText(StringUtil.getUvLevel(dailyForecast.getUltraviolet()));
            tvPres.setText(dailyForecast.getAirPressure().concat(getString(R.string.hPa)));
            tvSunRise.setText(dailyForecast.getSunRise());
            tvSunSet.setText(dailyForecast.getSunSet());
        }
    }
}
