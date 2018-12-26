package com.example.jy.jyweather.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.util.DrawableUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于我们界面
 * <p>
 * Created by txh on 2018/1/9.
 */

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rl_about_us)
    RelativeLayout rlAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        setContentView(R.layout.activity_about_us);
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
            rlAboutUs.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }
    }
}
