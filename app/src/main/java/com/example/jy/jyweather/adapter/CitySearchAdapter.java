package com.example.jy.jyweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.activity.WeatherActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * CitySearchAdapter
 * <p>
 * Created by Yang on 2018/1/8.
 */

public class CitySearchAdapter extends BaseAdapter {

    private Context context;

    private List<String> cityList;

    public CitySearchAdapter(Context context, List<String> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_city, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCityInfo.setText(cityList.get(i));
        holder.tvCityInfo.setOnClickListener(view -> {
            Set<String> citySet = JYApplication.getInstance().getCityDB().getCitySet();
            citySet = new HashSet<>(citySet);

            String city = cityList.get(i).split(" - ")[0];
            if (!citySet.contains(city)) {
                citySet.add(city);
                JYApplication.getInstance().getCityDB().setCitySet(citySet);
                if (citySet.size() == 1) {
                    JYApplication.getInstance().getCityDB().setDefaultCity(city);
                }
            }

            Intent intent = new Intent(context, WeatherActivity.class);
            intent.putExtra("city", city);
            context.startActivity(intent);
        });
        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.tv_city_info)
        TextView tvCityInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
