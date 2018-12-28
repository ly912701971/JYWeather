package com.example.jy.jyweather.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.jy.jyweather.BR;
import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.CommonAdapter;
import com.example.jy.jyweather.databinding.ActivityWeatherBinding;
import com.example.jy.jyweather.databinding.WeatherDailyForecastBinding;
import com.example.jy.jyweather.databinding.WeatherHeaderBinding;
import com.example.jy.jyweather.databinding.WeatherHourlyForecastBinding;
import com.example.jy.jyweather.databinding.WeatherLifeSuggestionBinding;
import com.example.jy.jyweather.databinding.WeatherNowBinding;
import com.example.jy.jyweather.entity.BasicBean;
import com.example.jy.jyweather.entity.DailyForecastBean;
import com.example.jy.jyweather.entity.HourlyForecastBean;
import com.example.jy.jyweather.entity.LifestyleBean;
import com.example.jy.jyweather.entity.NowBean;
import com.example.jy.jyweather.entity.UpdateBean;
import com.example.jy.jyweather.entity.WeatherBean;
import com.example.jy.jyweather.network.NetworkInterface;
import com.example.jy.jyweather.service.AutoUpdateService;
import com.example.jy.jyweather.util.JsonUtil;
import com.example.jy.jyweather.util.NotificationUtil;

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

        weatherBinding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable));
                } else {
                    weatherBinding.srlRefresh.setRefreshing(true);
                    getData(mCity);
                }
            }
        });

        if (JYApplication.getInstance().getCityDB().getAutoUpdate()) {
            startService(new Intent(this, AutoUpdateService.class));
        }
    }

    /**
     * 获取数据
     */
    private void getData(final String city) {
        weatherBinding.srlRefresh.setRefreshing(true);
        String dataText = JYApplication.getInstance().getCityDB().getData(city);

        if (!isNetworkAvailable()) {
            weatherBinding.srlRefresh.setRefreshing(false);
            showSnackBar(weatherBinding.srlRefresh, getString(R.string.network_unavailable));
            if (dataText != null) {
                handleData(dataText);
            }
            return;
        }

        NetworkInterface.queryWeatherData(city, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar(weatherBinding.srlRefresh, getString(R.string.data_unavailable));
                        weatherBinding.srlRefresh.setRefreshing(false);
                    }
                });

                String dataText = JYApplication.getInstance().getCityDB().getData(city);
                handleData(dataText);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                JYApplication.getInstance().getCityDB().setCityData(mCity, responseText);
                handleData(responseText);
            }
        });
    }

    private void handleData(String dataText) {
        WeatherBean weather = JsonUtil.handleWeatherResponse(dataText);
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
        if (JYApplication.getInstance().getCityDB().getNotification()) {
            NotificationUtil.openNotification(this);
        }
        JYApplication.getInstance().getCityDB().setCondCode(now.getCode());

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
                        lifeBinding.llComfort.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "cw":
                        lifeBinding.tvCarWash.setText(lifestyle.getBrief());
                        lifeBinding.llCarWash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "drsg":
                        lifeBinding.tvDress.setText(lifestyle.getBrief());
                        lifeBinding.llDress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "flu":
                        lifeBinding.tvFlu.setText(lifestyle.getBrief());
                        lifeBinding.llFlu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "sport":
                        lifeBinding.tvSport.setText(lifestyle.getBrief());
                        lifeBinding.llSport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "trav":
                        lifeBinding.tvTravel.setText(lifestyle.getBrief());
                        lifeBinding.llTravel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "uv":
                        lifeBinding.tvUv.setText(lifestyle.getBrief());
                        lifeBinding.llUv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "air":
                        lifeBinding.tvAir.setText(lifestyle.getBrief());
                        lifeBinding.llAir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    default:
                }
            }
        }
        headerBinding.rlHeader.setVisibility(View.VISIBLE);
        nowBinding.rlNow.setVisibility(View.VISIBLE);
        hourlyBinding.llHourlyForecast.setVisibility(View.VISIBLE);
        dailyBinding.llDailyForecast.setVisibility(View.VISIBLE);
        lifeBinding.llLifeSuggestion.setVisibility(View.VISIBLE);

        // 刷新数据动画
        ValueAnimator animator = ValueAnimator.ofInt(weatherBinding.svScroll.getHeight(), 0);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int currentValue = (int) valueAnimator.getAnimatedValue();
                weatherBinding.svScroll.smoothScrollTo(0, currentValue);
            }
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

        headerBinding.rlHeader.setVisibility(View.INVISIBLE);
        nowBinding.rlNow.setVisibility(View.INVISIBLE);
        hourlyBinding.llHourlyForecast.setVisibility(View.INVISIBLE);
        dailyBinding.llDailyForecast.setVisibility(View.INVISIBLE);
        lifeBinding.llLifeSuggestion.setVisibility(View.INVISIBLE);
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