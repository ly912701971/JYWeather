package com.example.jy.jyweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jy.jyweather.R;
import com.example.jy.jyweather.entity.HourlyForecastBean;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 逐时预报Adapter
 * <p>
 * Created by Yang on 2017/12/13.
 */

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private Context context;
    private List<HourlyForecastBean> hourlyForecasts;

    public HourlyForecastAdapter(Context context, List<HourlyForecastBean> hourlyForecasts) {
        this.context = context;
        this.hourlyForecasts = hourlyForecasts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly_forecast, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HourlyForecastBean data = hourlyForecasts.get(position);
        if (data != null) {
            holder.tvUpdateTime.setText(data.getTime().split(" ")[1]);
            holder.ivWeatherIcon.setImageResource(DrawableUtil.getCondIcon(data.getCode()));
            holder.tvTemp.setText(data.getTemperature().concat(context.getString(R.string.degree)));
            holder.tvCond.setText(data.getCondText());
            holder.tvWindDir.setText(data.getWindDirection());
            if (StringUtil.hasNumber(data.getWindScale())) {
                holder.tvWindSc.setText(data.getWindScale().concat(context.getString(R.string.level)));
            } else {
                holder.tvWindSc.setText(data.getWindScale());
            }
        }
    }

    @Override
    public int getItemCount() {
        return hourlyForecasts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_update_time)
        TextView tvUpdateTime;
        @BindView(R.id.iv_weather_icon)
        ImageView ivWeatherIcon;
        @BindView(R.id.tv_temp)
        TextView tvTemp;
        @BindView(R.id.tv_cond)
        TextView tvCond;
        @BindView(R.id.tv_wind_dir)
        TextView tvWindDir;
        @BindView(R.id.tv_wind_sc)
        TextView tvWindSc;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
