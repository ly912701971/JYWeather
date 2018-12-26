package com.example.jy.jyweather.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.DailyForecastAdapter;
import com.example.jy.jyweather.adapter.HourlyForecastAdapter;
import com.example.jy.jyweather.entity.BasicBean;
import com.example.jy.jyweather.entity.DailyForecastBean;
import com.example.jy.jyweather.entity.HourlyForecastBean;
import com.example.jy.jyweather.entity.LifestyleBean;
import com.example.jy.jyweather.entity.NowBean;
import com.example.jy.jyweather.entity.UpdateBean;
import com.example.jy.jyweather.entity.WeatherBean;
import com.example.jy.jyweather.network.NetworkInterface;
import com.example.jy.jyweather.service.AutoUpdateService;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.JsonUtil;
import com.example.jy.jyweather.util.NotificationUtil;
import com.example.jy.jyweather.util.StringUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_now_temp)
    TextView tvNowTemp;
    @BindView(R.id.tv_now_cond)
    TextView tvNowCond;
    @BindView(R.id.tv_now_wind_dir)
    TextView tvNowWindDir;
    @BindView(R.id.tv_now_wind_sc)
    TextView tvNowWindSc;
    @BindView(R.id.tv_now_hum)
    TextView tvNowHum;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.ll_brief)
    LinearLayout llBrief;
    @BindView(R.id.rv_hourly)
    RecyclerView rvHourly;
    @BindView(R.id.iv_now_icon)
    ImageView ivNowIcon;
    @BindView(R.id.rv_daily)
    RecyclerView rvDaily;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.tv_air)
    TextView tvAir;
    @BindView(R.id.tv_comfort)
    TextView tvComfort;
    @BindView(R.id.tv_car_wash)
    TextView tvCarWash;
    @BindView(R.id.tv_dress)
    TextView tvDress;
    @BindView(R.id.tv_flu)
    TextView tvFlu;
    @BindView(R.id.tv_sport)
    TextView tvSport;
    @BindView(R.id.tv_travel)
    TextView tvTravel;
    @BindView(R.id.tv_uv)
    TextView tvUv;
    @BindView(R.id.ll_air)
    LinearLayout llAir;
    @BindView(R.id.ll_comfort)
    LinearLayout llComfort;
    @BindView(R.id.ll_car_wash)
    LinearLayout llCarWash;
    @BindView(R.id.ll_dress)
    LinearLayout llDress;
    @BindView(R.id.ll_flu)
    LinearLayout llFlu;
    @BindView(R.id.ll_sport)
    LinearLayout llSport;
    @BindView(R.id.ll_travel)
    LinearLayout llTravel;
    @BindView(R.id.ll_uv)
    LinearLayout llUv;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.ll_main_background)
    LinearLayout llMainBackground;
    @BindView(R.id.ll_now)
    LinearLayout llNow;
    @BindView(R.id.ll_hourly_forecast)
    LinearLayout llHourlyForecast;
    @BindView(R.id.ll_daily_forecast)
    LinearLayout llDailyForecast;
    @BindView(R.id.ll_life_suggestion)
    LinearLayout llLifeSuggestion;
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;

    private BasicBean basic;
    private NowBean now;
    private UpdateBean update;
    private List<DailyForecastBean> dailyForecasts;
    private List<HourlyForecastBean> hourlyForecasts;
    private List<LifestyleBean> lifestyles;

    private MyHandler handler = new MyHandler();
    private String mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        ivHome.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        llBrief.setOnClickListener(this);
        tvNowTemp.setOnClickListener(this);
        tvCity.setOnClickListener(this);

        mCity = getIntent().getStringExtra("city");
        getData(mCity);

        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    showSnackBar(srlRefresh, getString(R.string.network_unavailable));
                } else {
                    srlRefresh.setRefreshing(true);
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
        srlRefresh.setRefreshing(true);
        String dataText = JYApplication.getInstance().getCityDB().getData(city);

        if (!isNetworkAvailable()) {
            srlRefresh.setRefreshing(false);
            showSnackBar(srlRefresh, getString(R.string.network_unavailable));
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
                        showSnackBar(srlRefresh, getString(R.string.data_unavailable));
                        srlRefresh.setRefreshing(false);
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

        // now
        if (basic != null) {
            tvCity.setText(basic.getCityName());
        }
        if (update != null) {
            tvUpdateTime.setText(update.getLoc().split(" ")[1]);
        }
        if (now != null) {
            llMainBackground.setBackgroundResource(DrawableUtil.getBackground(now.getCode()));
            JYApplication.getInstance().getCityDB().setCondCode(now.getCode());
            tvNowTemp.setText(now.getTemperature().concat(getString(R.string.degree)));
            ivNowIcon.setImageResource(DrawableUtil.getCondIcon(now.getCode()));
            tvNowCond.setText(now.getCondText());
            tvNowWindDir.setText(now.getWindDirection());
            if (StringUtil.hasNumber(now.getWindScale())) {
                tvNowWindSc.setText(now.getWindScale().concat(getString(R.string.level)));
            } else {
                tvNowWindSc.setText(now.getWindScale());
            }
            tvNowHum.setText(getString(R.string.humidity).concat(now.getRelativeHum()).concat(getString(R.string.percent)));
        }

        // hourly_forecast
        if (hourlyForecasts != null && !hourlyForecasts.isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvHourly.setLayoutManager(manager);
            rvHourly.setHasFixedSize(true);
            rvHourly.setAdapter(new HourlyForecastAdapter(this, hourlyForecasts));
        }

        // daily_forecast
        if (dailyForecasts != null && !dailyForecasts.isEmpty()) {
            LinearLayoutManager manager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    // 屏蔽RecyclerView的垂直滑动，否则与最外层ScrollView冲突，导致滑动卡顿
                    return false;
                }
            };
            rvDaily.setLayoutManager(manager);
            rvDaily.setHasFixedSize(true);
            rvDaily.setAdapter(new DailyForecastAdapter(this, dailyForecasts));
        }

        // lifestyle
        if (lifestyles != null && !lifestyles.isEmpty()) {
            for (final LifestyleBean lifestyle : lifestyles) {
                switch (lifestyle.getType()) {
                    case "comf":
                        tvComfort.setText(lifestyle.getBrief());
                        llComfort.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "cw":
                        tvCarWash.setText(lifestyle.getBrief());
                        llCarWash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "drsg":
                        tvDress.setText(lifestyle.getBrief());
                        llDress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "flu":
                        tvFlu.setText(lifestyle.getBrief());
                        llFlu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "sport":
                        tvSport.setText(lifestyle.getBrief());
                        llSport.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "trav":
                        tvTravel.setText(lifestyle.getBrief());
                        llTravel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "uv":
                        tvUv.setText(lifestyle.getBrief());
                        llUv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(lifestyle.getText()).show();
                            }
                        });
                        break;

                    case "air":
                        tvAir.setText(lifestyle.getBrief());
                        llAir.setOnClickListener(new View.OnClickListener() {
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
        llLifeSuggestion.setVisibility(View.VISIBLE);
        llDailyForecast.setVisibility(View.VISIBLE);
        llHourlyForecast.setVisibility(View.VISIBLE);
        llNow.setVisibility(View.VISIBLE);

        // 刷新数据动画
        ValueAnimator animator = ValueAnimator.ofInt(svScroll.getHeight(), 0);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int currentValue = (int) valueAnimator.getAnimatedValue();
                svScroll.smoothScrollTo(0, currentValue);
            }
        });
        animator.start();

        srlRefresh.setRefreshing(false);
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
                    intent.putExtra("update_time", update.getLoc().split(" ")[1]);
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

        tvCity.setText("");
        llNow.setVisibility(View.INVISIBLE);
        llHourlyForecast.setVisibility(View.INVISIBLE);
        llDailyForecast.setVisibility(View.INVISIBLE);
        llLifeSuggestion.setVisibility(View.INVISIBLE);
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