package com.example.jy.jyweather.activity;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.CitySearchAdapter;
import com.example.jy.jyweather.databinding.ActivityChooseCityBinding;
import com.example.jy.jyweather.util.DrawableUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        if (spCode != null) {
            binding.rlChooseBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etSearch.getText().length() > 0) {
                    binding.etSearch.setText("");
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
                    CitySearchAdapter adapter = new CitySearchAdapter(ChooseCityActivity.this, searchResult);
                    binding.lvSearchResult.setAdapter(adapter);
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
        // 百度地图自动定位
        mClient = new LocationClient(JYApplication.getInstance());
        mClient.registerLocationListener(new MyLocationListener());
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
        binding.tvLocation.setText(getResources().getString(R.string.locate_unknown));
        binding.tvLocation.setClickable(false);
        showSnackBar(binding.tvLocation, getString(R.string.permission_denied));
    }

    private void addCity(String city) {
        Set<String> citySet = JYApplication.getInstance().getCityDB().getCitySet();
        citySet = new HashSet<>(citySet);
        citySet.add(city);
        JYApplication.getInstance().getCityDB().setCitySet(citySet);
        if (citySet.size() == 1) {
            JYApplication.getInstance().getCityDB().setDefaultCity(city);
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

        String city = null;
        switch (v.getId()) {
            case R.id.tv_location:
                if (locationCity != null) {
                    city = locationCity;
                }
                break;
            default:
                city = String.valueOf(((TextView) v).getText());
        }

        addCity(city);
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mClient.stop();

            String city = location.getCity();
            if (city == null) {// 返回为空显示"定位失败"
                binding.tvLocation.setText(getResources().getString(R.string.locate_unknown));
                binding.tvLocation.setClickable(false);
            } else {
                binding.tvLocation.setText(city);
                locationCity = location.getCity().replace("市", "")
                        .replace("区", "")
                        .replace("县", "");
            }
        }
    }
}
