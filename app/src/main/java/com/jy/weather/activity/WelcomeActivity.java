package com.jy.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jy.weather.JYApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动界面
 * <p>
 * Created by liu on 2018/1/8.
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                jumpActivity();
            }
        };
        // 使用timer.schedule（）方法调用timerTask，定时1秒后执行run
        timer.schedule(timerTask, 1000);
    }

    private void jumpActivity() {
        String city = JYApplication.cityDB.getDefaultCity();
        if (city == null) {
            startActivity(new Intent(this, ChooseCityActivity.class));
        } else {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        }
        finish();
    }
}
