package com.jy.weather.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.activity.WeatherActivity;
import com.jy.weather.databinding.ItemSearchCityBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CitySearchAdapter
 * <p>
 * Created by Yang on 2018/1/8.
 */
public class CitySearchAdapter extends BaseAdapter {

    private List<String> cityList;
    private LayoutInflater inflater;

    public CitySearchAdapter(Context context, List<String> cityList) {
        this.cityList = cityList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public String getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ItemSearchCityBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_search_city, viewGroup, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemSearchCityBinding) convertView.getTag();
        }

        binding.setCityInfo(getItem(i));
        binding.tvCityInfo.setOnClickListener(view -> {
            Set<String> citySet = new HashSet<>(JYApplication.cityDB.getCitySet());
            String city = cityList.get(i).split(" - ")[0];
            if (!citySet.contains(city)) {
                citySet.add(city);
                JYApplication.cityDB.setCitySet(citySet);
                if (citySet.size() == 1) {
                    JYApplication.cityDB.setDefaultCity(city);
                }
            }

            Intent intent = new Intent(JYApplication.INSTANCE, WeatherActivity.class);
            intent.putExtra("city", city);
            JYApplication.INSTANCE.startActivity(intent);
        });
        return convertView;
    }
}
