package com.example.jy.jyweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;

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

        // 创建Timer对象
        Timer timer = new Timer();
        // 创建TimerTask对象
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                jumpActivity();
            }
        };
        // 使用timer.schedule（）方法调用timerTask，定时3秒后执行run
        timer.schedule(timerTask, 2000);
    }

    private void jumpActivity() {
        String city = JYApplication.getInstance().getCityDB().getDefaultCity();
        if (city == null) {
            startActivity(new Intent(this, ChooseCityActivity.class));
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        }
        finish();
    }

}
