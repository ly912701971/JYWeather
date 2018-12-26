package com.example.jy.jyweather.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.CitySearchAdapter;
import com.example.jy.jyweather.util.DrawableUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class ChooseCityActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.ll_hot_city)
    LinearLayout llHotCity;
    @BindView(R.id.lv_search_result)
    ListView lvSearchResult;
    @BindView(R.id.rl_choose_background)
    RelativeLayout rlChooseBackground;
    @BindView(R.id.ll_search_result)
    LinearLayout llSearchResult;

    private String locationCity;
    private List<String> cityList;
    private List<String> searchResult;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    LocationClient mClient;
    private MyLocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        setContentView(R.layout.activity_choose_city);
        ButterKnife.bind(this);

        if (!EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "您需要同意以下权限方可使用定位功能", 1, PERMISSIONS);
        } else {
            locate();
        }
        init();
    }

    private void init() {
        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        if (spCode != null) {
            rlChooseBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSearch.getText().length() > 0) {
                    etSearch.setText("");
                } else if (JYApplication.getInstance().getCityDB().getDefaultCity() == null) {
                    addCity("北京");
                    finish();
                } else {
                    finish();
                }
            }
        });

        // 添加全国城市列表
        cityList = Arrays.asList(getResources().getStringArray(R.array.national_cities_list));
        searchResult = new ArrayList<>();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {// 有输入时查询匹配结果
                    searchResult.clear();
                    for (String cityInfo : cityList) {
                        if (cityInfo.contains(editable)) {
                            searchResult.add(cityInfo);
                        }
                    }
                    CitySearchAdapter adapter = new CitySearchAdapter(ChooseCityActivity.this, searchResult);
                    lvSearchResult.setAdapter(adapter);
                    llSearchResult.setVisibility(View.VISIBLE);
                    llHotCity.setVisibility(View.GONE);
                } else {
                    llHotCity.setVisibility(View.VISIBLE);
                    llSearchResult.setVisibility(View.GONE);
                }
            }
        });
    }

    private void locate() {
        // 百度地图自动定位
        mListener = new MyLocationListener();
        mClient = new LocationClient(JYApplication.getInstance());
        mClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mClient.setLocOption(option);
        mClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size() == PERMISSIONS.length) {
            locate();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        tvLocation.setText(getResources().getString(R.string.locate_unknown));
        tvLocation.setClickable(false);
        showSnackBar(tvLocation, getString(R.string.permission_denied));
    }

    @OnClick({R.id.tv_location, R.id.tv_beijing, R.id.tv_tianjin, R.id.tv_shanghai, R.id.tv_chongqing, R.id.tv_shenyang, R.id.tv_dalian, R.id.tv_changchun, R.id.tv_haerbin, R.id.tv_zhengzhou, R.id.tv_wuhan, R.id.tv_changsha, R.id.tv_guangzhou, R.id.tv_shenzhen, R.id.tv_nanjing, R.id.tv_hangzhou, R.id.tv_dongguan, R.id.tv_chengdu, R.id.tv_qingdao, R.id.tv_suzhou, R.id.tv_xiamen})
    public void onViewClicked(View view) {
        if (!isNetworkAvailable()) {
            showSnackBar(view, getString(R.string.network_unavailable));
            return;
        }

        String city = null;
        switch (view.getId()) {
            case R.id.tv_location:
                if (locationCity != null) {
                    city = locationCity;
                }
                break;
            case R.id.tv_beijing:
                city = "北京";
                break;
            case R.id.tv_tianjin:
                city = "天津";
                break;
            case R.id.tv_shanghai:
                city = "上海";
                break;
            case R.id.tv_chongqing:
                city = "重庆";
                break;
            case R.id.tv_shenyang:
                city = "沈阳";
                break;
            case R.id.tv_dalian:
                city = "大连";
                break;
            case R.id.tv_changchun:
                city = "长春";
                break;
            case R.id.tv_haerbin:
                city = "哈尔滨";
                break;
            case R.id.tv_zhengzhou:
                city = "郑州";
                break;
            case R.id.tv_wuhan:
                city = "武汉";
                break;
            case R.id.tv_changsha:
                city = "长沙";
                break;
            case R.id.tv_guangzhou:
                city = "广州";
                break;
            case R.id.tv_shenzhen:
                city = "深圳";
                break;
            case R.id.tv_nanjing:
                city = "南京";
                break;
            case R.id.tv_hangzhou:
                city = "杭州";
                break;
            case R.id.tv_dongguan:
                city = "东莞";
                break;
            case R.id.tv_chengdu:
                city = "成都";
                break;
            case R.id.tv_qingdao:
                city = "青岛";
                break;
            case R.id.tv_suzhou:
                city = "苏州";
                break;
            case R.id.tv_xiamen:
                city = "厦门";
                break;
            default:
        }

        addCity(city);
    }

    private void addCity(String city) {
        Set<String> citySet = JYApplication.getInstance().getCityDB().getCitySet();
        citySet = new HashSet<>(citySet);
        citySet.add(city);
        JYApplication.getInstance().getCityDB().setCitySet(citySet);
        if (citySet.size() == 1) {
            JYApplication.getInstance().getCityDB().setDefaultCity(city);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
        finish();
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mClient.stop();

            String city = location.getCity();
            if (city == null) {// 返回为空显示"定位失败"
                tvLocation.setText(getResources().getString(R.string.locate_unknown));
                tvLocation.setClickable(false);
            } else {
                tvLocation.setText(city);
                locationCity = location.getCity().replace("市", "")
                        .replace("区", "")
                        .replace("县", "");
            }
        }
    }
}
