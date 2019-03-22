package com.jy.weather.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.adapter.CitySearchAdapter;
import com.jy.weather.databinding.ActivityChooseCityBinding;
import com.jy.weather.util.DrawableUtil;
import com.jy.weather.util.GpsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class ChooseCityActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    ActivityChooseCityBinding binding;

    private String locationCity;
    private List<String> cityList;
    private List<String> searchResult;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    LocationClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_city);
        binding.setClickListener(this);

        if (!EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "您需要同意以下权限方可使用定位功能", 1, PERMISSIONS);
        } else {
            locate();
        }
        init();
    }

    private void init() {
        String spCode = JYApplication.cityDB.getCondCode();
        if (spCode != null) {
            binding.rlChooseBackground.setBackgroundResource(DrawableUtil.INSTANCE.getBackground(spCode));
        }

        binding.ivBack.setOnClickListener(view -> {
            if (binding.etSearch.getText().length() > 0) {
                binding.etSearch.setText("");
            } else if (JYApplication.cityDB.getDefaultCity() == null) {
                addCity("北京");
                finish();
            } else {
                finish();
            }
        });

        // 添加全国城市列表
        cityList = Arrays.asList(getResources().getStringArray(R.array.national_cities_list));
        searchResult = new ArrayList<>();
        CitySearchAdapter adapter = new CitySearchAdapter(ChooseCityActivity.this, searchResult);
        binding.lvSearchResult.setAdapter(adapter);

        binding.etSearch.addTextChangedListener(new TextWatcher() {
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
                    adapter.notifyDataSetChanged();
                    binding.llSearchResult.setVisibility(View.VISIBLE);
                    binding.llHotCity.setVisibility(View.GONE);
                } else {
                    binding.llHotCity.setVisibility(View.VISIBLE);
                    binding.llSearchResult.setVisibility(View.GONE);
                }
            }
        });
    }

    private void locate() {
        if (GpsUtil.INSTANCE.isOpen(JYApplication.INSTANCE)) {
            // 百度地图自动定位
            mClient = new LocationClient(JYApplication.INSTANCE);
            mClient.registerLocationListener(new MyLocationListener());
            LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            option.setOpenGps(true);
            mClient.setLocOption(option);
            mClient.start();
        } else {
            onLocationFailed();
            showGpsNotOpen();
        }

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
        onLocationFailed();
        showPermissionDenied();
    }

    private void addCity(String city) {
        HashSet<String> citySet = new HashSet<>(JYApplication.cityDB.getCitySet());
        citySet.add(city);
        JYApplication.cityDB.setCitySet(citySet);
        if (citySet.size() == 1) {
            JYApplication.cityDB.setDefaultCity(city);
        }

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (!isNetworkAvailable()) {
            showSnackBar(v, getString(R.string.network_unavailable));
            return;
        }

        String city = "";
        switch (v.getId()) {
            case R.id.tv_location:
                if (!GpsUtil.INSTANCE.isOpen(JYApplication.INSTANCE)) {
                    showGpsNotOpen();
                    return;
                } else {
                    if (locationCity != null) {
                        city = locationCity;
                    }
                }
                break;

            default:
                city = String.valueOf(((TextView) v).getText());
        }

        addCity(city.replace("市", ""));
    }

    private void showGpsNotOpen() {
        Snackbar snackbar = Snackbar.make(binding.tvLocation,
                getResources().getString(R.string.locate_failed),
                Snackbar.LENGTH_LONG);
        snackbar.setAction("前往打开", v -> {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            startActivityForResult(intent, 2);
            Intent intent = new Intent("com.jy.weather");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            startActivity(intent);
        });
        snackbar.show();
    }

    private void showPermissionDenied() {
        Snackbar snackbar = Snackbar.make(binding.tvLocation,
                getResources().getString(R.string.locate_failed),
                Snackbar.LENGTH_LONG);
        snackbar.setAction("前往打开", v -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        });
        snackbar.show();
    }

    private void onLocationSuccessful(String city) {
        binding.tvLocation.setText(city);
        locationCity = city.replace("市", "")
                .replace("区", "")
                .replace("县", "");
    }

    private void onLocationFailed() {
        binding.tvLocation.setText(getResources().getString(R.string.locate_unknown));
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mClient.stop();

            String city = location.getCity();
            if (city == null) {
                onLocationFailed();
            } else {
                onLocationSuccessful(city);
            }
        }
    }
}
