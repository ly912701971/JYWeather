package com.jy.weather.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.adapter.CityListAdapter;
import com.jy.weather.databinding.ActivityCityManageBinding;
import com.jy.weather.entity.WeatherBean;
import com.jy.weather.util.DrawableUtil;
import com.jy.weather.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CityManageActivity extends BaseActivity {

    private ActivityCityManageBinding binding;

    private Set<String> citySet;
    private List<Map<String, String>> cityList;
    private CityListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_manage);

        init();
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        String spCode = JYApplication.cityDB.getCondCode();
        if (spCode != null) {
            binding.rlCityManageBackground.setBackgroundResource(DrawableUtil.INSTANCE.getBackground(spCode));
        }

        cityList = new ArrayList<>();
        citySet = new HashSet<>(JYApplication.cityDB.getCitySet());
        String weatherData;
        Map<String, String> cityData;
        for (String city : citySet) {
            weatherData = JYApplication.cityDB.getData(city);
            WeatherBean weather = JsonUtil.INSTANCE.handleWeatherResponse(weatherData);
            if (weather != null) {
                cityData = new HashMap<>();
                cityData.put("city", weather.getBasic().getCityName());
                cityData.put("cond_code", weather.getNow().getCode());
                cityData.put("admin_area", weather.getBasic().getParentCity()
                        .concat(" - ")
                        .concat(weather.getBasic().getAdminArea()));
                cityData.put("temp_scope", weather.getDailyForecasts().get(0).getMinTemp()
                        .concat(" ~ ")
                        .concat(weather.getDailyForecasts().get(0).getMaxTemp())
                        .concat("C"));
                cityList.add(cityData);
            }
        }
        adapter = new CityListAdapter(this, cityList);
        binding.smlvCityList.setAdapter(adapter);

        // SwipeMenuListView构造器
        binding.smlvCityList.setMenuCreator(menu -> {
            // "常驻"item
            SwipeMenuItem defaultItem = new SwipeMenuItem(CityManageActivity.this);
            defaultItem.setTitle("常驻");
            defaultItem.setTitleSize(16);
            defaultItem.setBackground(R.color.item_default);
            defaultItem.setTitleColor(Color.WHITE);
            defaultItem.setWidth(240);
            menu.addMenuItem(defaultItem);

            // "删除城市"item
            SwipeMenuItem deleteItem = new SwipeMenuItem(CityManageActivity.this);
            deleteItem.setTitle("删除");
            deleteItem.setTitleSize(16);
            deleteItem.setBackground(R.color.item_delete);
            deleteItem.setTitleColor(Color.WHITE);
            deleteItem.setWidth(240);
            menu.addMenuItem(deleteItem);
        });
        // SwipeMenuListView子项点击事件
        binding.smlvCityList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(CityManageActivity.this, WeatherActivity.class);
            intent.putExtra("city", cityList.get(i).get("city"));
            startActivity(intent);
        });
        // SwipeMenuListView向左滑动
        binding.smlvCityList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // SwipeMenuListView子项菜单点击事件
        binding.smlvCityList.setOnMenuItemClickListener((position, menu, index) -> {
            String city = cityList.get(position).get("city");
            if (city == null) {
                return false;
            }
            String defaultCity = JYApplication.cityDB.getDefaultCity();
            switch (index) {
                case 0:// 点击"常驻"
                    if (!city.equals(defaultCity)) {
                        JYApplication.cityDB.setDefaultCity(city);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 1:// 点击"删除"
                    if (citySet.size() == 1) {// 只有一个城市
                        showSnackBar(binding.smlvCityList, "请至少保留一个城市");
                    } else if (city.equals(defaultCity)) {// 删除常驻城市
                        showSnackBar(binding.smlvCityList, "您不能删除常驻城市");
                    } else {
                        for (Map<String, String> data : cityList) {
                            if (city.equals(data.get("city"))) {
                                cityList.remove(data);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        citySet.remove(city);
                        JYApplication.cityDB.setCitySet(citySet);
                        JYApplication.cityDB.removeCity(city);
                    }
                    break;
                default:
            }
            return false;// false:关闭菜单
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_choose_city) {
            startActivity(new Intent(this, ChooseCityActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
