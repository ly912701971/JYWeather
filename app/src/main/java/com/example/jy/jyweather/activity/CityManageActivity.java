package com.example.jy.jyweather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.adapter.CityListAdapter;
import com.example.jy.jyweather.entity.WeatherBean;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityManageActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rl_city_manage_background)
    RelativeLayout rlCityManageBackground;
    @BindView(R.id.smlv_city_list)
    SwipeMenuListView smlvCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTrans();
        setStatusBarColor();
        setContentView(R.layout.activity_city_manage);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String spCode = JYApplication.getInstance().getCityDB().getCondCode();
        Set<String> citySetClone = JYApplication.getInstance().getCityDB().getCitySet();
        final Set<String> citySet = new HashSet<>(citySetClone);
        if (spCode != null) {
            rlCityManageBackground.setBackgroundResource(DrawableUtil.getBackground(spCode));
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final List<Map<String, String>> cityList = new ArrayList<>();
        final CityListAdapter adapter = new CityListAdapter(this, cityList);
        smlvCityList.setAdapter(adapter);

        // SwipeMenuListView构造器
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
            }
        };
        smlvCityList.setMenuCreator(creator);

        // SwipeMenuListView子项点击事件
        smlvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CityManageActivity.this, WeatherActivity.class);
                intent.putExtra("city", cityList.get(i).get("city"));
                startActivity(intent);
            }
        });

        // 向左滑动
        smlvCityList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        // SwipeMenuListView子项菜单点击事件
        smlvCityList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String city = cityList.get(position).get("city");
                String defaultCity = JYApplication.getInstance().getCityDB().getDefaultCity();
                switch (index) {
                    case 0:// 点击"常驻"
                        if (!city.equals(defaultCity)) {
                            JYApplication.getInstance().getCityDB().setDefaultCity(city);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 1:// 点击"删除"
                        if (citySet.size() == 1) {// 只有一个城市
                            showSnackBar(smlvCityList, "请至少保留一个城市");
                        } else if (city.equals(defaultCity)) {// 删除常驻城市
                            showSnackBar(smlvCityList, "您不能删除常驻城市");
                        } else {
                            for (Map<String, String> data : cityList) {
                                if (data.get("city").equals(city)) {
                                    cityList.remove(data);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            citySet.remove(city);
                            JYApplication.getInstance().getCityDB().setCitySet(citySet);
                            JYApplication.getInstance().getCityDB().removeCity(city);
                        }
                        break;
                    default:
                }
                return false;// false:关闭菜单
            }
        });

        String data;
        Map<String, String> cityData;
        for (String city : citySet) {
            data = JYApplication.getInstance().getCityDB().getData(city);
            WeatherBean weather = JsonUtil.handleWeatherResponse(data);
            if (weather != null) {
                cityData = new HashMap<>();
                try {
                    cityData.put("city", weather.getBasic().getCityName());
                    cityData.put("cond_code", weather.getNow().getCode());
                    cityData.put("admin_area", weather.getBasic().getParentCity()
                            .concat(" - ")
                            .concat(weather.getBasic().getAdminArea()));
                    cityData.put("temp_scope", weather.getDailyForecasts().get(0).getMinTemp()
                            .concat(" ~ ")
                            .concat(weather.getDailyForecasts().get(0).getMaxTemp())
                            .concat(getString(R.string.c_degree)));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                cityList.add(cityData);
            }
        }
        adapter.notifyDataSetChanged();
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
