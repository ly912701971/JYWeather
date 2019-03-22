package com.jy.weather.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jy.weather.JYApplication;
import com.jy.weather.R;
import com.jy.weather.databinding.ItemCityBinding;

import java.util.List;
import java.util.Map;

/**
 * 城市列表adapter
 * <p>
 * Created by Yang on 2018/1/6.
 */
public class CityListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Map<String, String>> cityList;

    public CityListAdapter(Context context, List<Map<String, String>> cityList) {
        inflater = LayoutInflater.from(context);
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Map<String, String> getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String defaultCity = JYApplication.cityDB.getDefaultCity();
        ItemCityBinding binding;
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_city, viewGroup, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (ItemCityBinding) view.getTag();
        }

        binding.setMap(getItem(i));
        binding.setDefaultName(defaultCity);
        return view;
    }
}
