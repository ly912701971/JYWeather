package com.jy.weather.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.jy.weather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity<Data> extends AppCompatActivity implements IBaseActivity<Data> {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.view_container)
    protected FrameLayout mViewContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 强制竖屏
    }

    protected void initToolbar(int resId, String title) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(resId);
            mToolbar.setTitle(title);
        }
    }

    protected void setActivityView(int resId) {
        View view = LayoutInflater.from(this).inflate(resId, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewContainer.addView(view, lp);
    }

    /**
     * 设置状态栏颜色为目标颜色
     *
     * @param color 目标颜色
     */
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(color);
        }
    }

    /**
     * 判断网络是否正常
     *
     * @return 返回true代表网络正常，返回false代表网络异常
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (NetworkInfo info : networkInfo) {
                    if (info.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(mToolbar, msg, Snackbar.LENGTH_LONG).show();
    }
}
