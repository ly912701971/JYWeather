package com.jy.weather.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.jy.weather.BR;
import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.adapter.CommonAdapter;
import com.jy.weather.databinding.ActivityWeatherBinding;
import com.jy.weather.databinding.WeatherDailyForecastBinding;
import com.jy.weather.databinding.WeatherHeaderBinding;
import com.jy.weather.databinding.WeatherHourlyForecastBinding;
import com.jy.weather.databinding.WeatherLifeSuggestionBinding;
import com.jy.weather.databinding.WeatherNowBinding;
import com.jy.weather.entity.BasicBean;
import com.jy.weather.entity.DailyForecastBean;
import com.jy.weather.entity.HourlyForecastBean;
import com.jy.weather.entity.LifestyleBean;
import com.jy.weather.entity.NowBean;
import com.jy.weather.entity.UpdateBean;
import com.jy.weather.entity.WeatherBean;
import com.jy.weather.network.NetworkInterface;
import com.jy.weather.service.AutoUpdateService;
import com.jy.weather.util.JsonUtil;
import com.jy.weather.util.NotificationUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity implements View.OnClickListener {

    private BasicBean basic;
    private NowBean now;
    private UpdateBean update;
    private List<DailyForecastBean> dailyForecasts;
    private List<HourlyForecastBean> hourlyForecasts;
    private List<LifestyleBean> lifestyles;

    private MyHandler handler = new MyHandler();
    private String mCity;

    private ActivityWeatherBinding weatherBinding;
    private WeatherHeaderBinding headerBinding;
    private WeatherNowBinding nowBinding;
    private WeatherHourlyForecastBinding hourlyBinding;
    private WeatherDailyForecastBinding dailyBinding;
    private WeatherLifeSuggestionBinding lifeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        weatherBinding = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        headerBinding = weatherBinding.headerPart;
        nowBinding = weatherBinding.nowPart;
        hourlyBinding = weatherBinding.hourlyForecastPart;
        dailyBinding = weatherBinding.dailyForecastPart;
        lifeBinding = weatherBinding.lifeSuggestionPart;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        headerBinding.ivHome.setOnClickListener(this);
        headerBinding.ivSetting.setOnClickListener(this);
        headerBinding.tvCity.setOnClickListener(this);
        nowBinding.llBrief.setOnClickListener(this);
        nowBinding.tvNowTemp.setOnClickListener(this);

        mCity = getIntent().getStringExtra("city");
        getData(mCity);

        weatherBinding.srlRefresh.setOnRefreshListener(() -> {
            if (!isNetworkAvailable()) {
                showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable));
            } else {
                weatherBinding.srlRefresh.setRefreshing(true);
                getData(mCity);
            }
        });

        if (JYApplication.cityDB.getAutoUpdate()) {
            startService(new Intent(this, AutoUpdateService.class));
        }
    }

    /**
     * 获取数据
     */
    private void getData(final String city) {
        weatherBinding.srlRefresh.setRefreshing(true);
        String dataText = JYApplication.cityDB.getData(city);

        if (!isNetworkAvailable()) {
            weatherBinding.srlRefresh.setRefreshing(false);
            showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable));
            if (dataText != null) {
                handleData(dataText);
            }
            return;
        }

        NetworkInterface.INSTANCE.queryWeatherData(city, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    showSnackBar(weatherBinding.srlRefresh, getString(R.string.data_unavailable));
                    weatherBinding.srlRefresh.setRefreshing(false);
                });

                String dataText = JYApplication.cityDB.getData(city);
                handleData(dataText);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                JYApplication.cityDB.setCityData(mCity, responseText);
                handleData(responseText);
            }
        });
    }

    private void handleData(String dataText) {
        WeatherBean weather = JsonUtil.INSTANCE.handleWeatherResponse(dataText);
        if (weather != null && weather.getStatus().equals("ok")) {
            basic = weather.getBasic();
            now = weather.getNow();
            update = weather.getUpdate();
            dailyForecasts = weather.getDailyForecasts();
            hourlyForecasts = weather.getHourlyForecasts();
            lifestyles = weather.getLifestyles();

            // 发送回调成功消息
            Message message = handler.obtainMessage();
            message.what = 0;
            message.sendToTarget();
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        if (JYApplication.cityDB.getNotification()) {
            NotificationUtil.INSTANCE.openNotification(this);
        }
        JYApplication.cityDB.setCondCode(now.getCode());

        headerBinding.setBasic(basic);
        nowBinding.setNow(now);
        nowBinding.setUpdateTime(update.getLoc());
        weatherBinding.llMainBackground.setBackgroundResource(now.getNowBackground());

        // hourly_forecast
        LinearLayoutManager hourlyManager = new LinearLayoutManager(this);
        hourlyManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hourlyBinding.rvHourly.setLayoutManager(hourlyManager);
        hourlyBinding.rvHourly.setHasFixedSize(true);
        hourlyBinding.rvHourly.setAdapter(new CommonAdapter<>(hourlyForecasts,
                R.layout.item_hourly_forecast,
                BR.hourlyForecast));

        // daily_forecast
        LinearLayoutManager dailyManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                // 屏蔽RecyclerView的垂直滑动，否则与最外层ScrollView冲突，导致滑动卡顿
                return false;
            }
        };
        dailyBinding.rvDaily.setLayoutManager(dailyManager);
        dailyBinding.rvDaily.setHasFixedSize(true);
        dailyBinding.rvDaily.setAdapter(new CommonAdapter<>(dailyForecasts,
                R.layout.item_daily_forecast,
                BR.dailyForecast));

        // lifestyle
        if (lifestyles != null && !lifestyles.isEmpty()) {
            for (final LifestyleBean lifestyle : lifestyles) {
                switch (lifestyle.getType()) {
                    case "comf":
                        lifeBinding.tvComfort.setText(lifestyle.getBrief());
                        lifeBinding.llComfort.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "cw":
                        lifeBinding.tvCarWash.setText(lifestyle.getBrief());
                        lifeBinding.llCarWash.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "drsg":
                        lifeBinding.tvDress.setText(lifestyle.getBrief());
                        lifeBinding.llDress.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "flu":
                        lifeBinding.tvFlu.setText(lifestyle.getBrief());
                        lifeBinding.llFlu.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "sport":
                        lifeBinding.tvSport.setText(lifestyle.getBrief());
                        lifeBinding.llSport.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "trav":
                        lifeBinding.tvTravel.setText(lifestyle.getBrief());
                        lifeBinding.llTravel.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "uv":
                        lifeBinding.tvUv.setText(lifestyle.getBrief());
                        lifeBinding.llUv.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    case "air":
                        lifeBinding.tvAir.setText(lifestyle.getBrief());
                        lifeBinding.llAir.setOnClickListener(view -> createDialog(lifestyle.getText()).show());
                        break;

                    default:
                }
            }
        }
        changeVisibility(View.VISIBLE);

        // 刷新数据动画
        ValueAnimator animator = ValueAnimator.ofInt(weatherBinding.svScroll.getHeight(), 0);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            int currentValue = (int) valueAnimator.getAnimatedValue();
            weatherBinding.svScroll.smoothScrollTo(0, currentValue);
        });
        animator.start();

        weatherBinding.srlRefresh.setRefreshing(false);
    }

    private Dialog createDialog(String message) {
        Dialog dialog = new Dialog(this, R.style.SimpleDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.dialog_lifestyle, null);
        ((TextView) view.findViewById(R.id.tv_message)).setText(message);
        dialog.addContentView(view, getDialogParams(6));
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_now_temp:// 点击当前温度
            case R.id.ll_brief:// 点击天气概况
                if (now != null) {
                    Intent intent = new Intent(this, TodayActivity.class);
                    intent.putExtra("location", basic.getCityName());
                    intent.putExtra("update_time", update.getLoc());
                    intent.putExtra("now", now);
                    intent.putExtra("daily_forecast", dailyForecasts.get(0));
                    startActivity(intent);
                }
                break;

            case R.id.iv_home:// 点击城市管理
            case R.id.tv_city:// 点击城市名
                startActivity(new Intent(this, CityManageActivity.class));
                break;

            case R.id.iv_setting:// 点击设置
                startActivity(new Intent(this, SettingActivity.class));
                break;

            default:
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);

        String city = intent.getStringExtra("city");
        if (mCity.equals(city)) {
            return;
        } else {
            mCity = city;
        }

        changeVisibility(View.INVISIBLE);
        getData(mCity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                mCity = data.getStringExtra("city");
                getData(mCity);
            }
        }
    }

    private void changeVisibility(int visibility) {
        headerBinding.rlHeader.setVisibility(visibility);
        nowBinding.rlNow.setVisibility(visibility);
        hourlyBinding.llHourlyForecast.setVisibility(visibility);
        dailyBinding.llDailyForecast.setVisibility(visibility);
        lifeBinding.llLifeSuggestion.setVisibility(visibility);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回桌面而不是退出应用
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initView();
                    break;
                default:
            }
        }
    }
}