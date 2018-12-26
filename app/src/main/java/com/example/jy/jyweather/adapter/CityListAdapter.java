package com.example.jy.jyweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jy.jyweather.JYApplication;
import com.example.jy.jyweather.R;
import com.example.jy.jyweather.util.DrawableUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 城市列表adapter
 * <p>
 * Created by Yang on 2018/1/6.
 */

public class CityListAdapter extends BaseAdapter {

    private Context context;

    private List<Map<String, String>> cityList;

    public CityListAdapter(Context context, List<Map<String, String>> cityList) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        String defaultCity = JYApplication.getInstance().getCityDB().getDefaultCity();
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_city, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Map<String, String> data = cityList.get(i);
        holder.ivWeatherIcon.setImageResource(DrawableUtil.getCondIcon(data.get("cond_code")));
        holder.tvCity.setText(data.get("city"));
        holder.tvAdminArea.setText(data.get("admin_area"));
        holder.tvTempScope.setText(data.get("temp_scope"));
        if (data.get("city").equals(defaultCity)) {
            holder.ivDefaultCity.setVisibility(View.VISIBLE);
        } else {
            holder.ivDefaultCity.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.iv_weather_icon)
        ImageView ivWeatherIcon;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.iv_default_city)
        ImageView ivDefaultCity;
        @BindView(R.id.tv_admin_area)
        TextView tvAdminArea;
        @BindView(R.id.tv_temp_scope)
        TextView tvTempScope;
        @BindView(R.id.ll_city_item)
        LinearLayout llCityItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
