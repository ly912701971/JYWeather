package com.example.jy.jyweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jy.jyweather.R;
import com.example.jy.jyweather.entity.DailyForecastBean;
import com.example.jy.jyweather.util.DrawableUtil;
import com.example.jy.jyweather.util.StringUtil;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 3天预报Adapter
 * <p>
 * Created by Yang on 2017/12/14.
 */

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private Context context;
    private List<DailyForecastBean> dailyForecasts;

    public DailyForecastAdapter(Context context, List<DailyForecastBean> dailyForecasts) {
        this.context = context;
        this.dailyForecasts = dailyForecasts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DailyForecastBean data = dailyForecasts.get(position);
        if (data != null) {
            String[] dates = data.getDate().split("-");
            holder.tvDate.setText(dates[1].concat("-").concat(dates[2]));
            try {
                holder.tvWeekday.setText(StringUtil.getWeekday(data.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.ivCondDay.setImageResource(DrawableUtil.getCondIcon(data.getDayCondCode()));
            holder.tvCondDay.setText(data.getDayCondText());
            holder.tvTempMax.setText(data.getMaxTemp().concat(context.getString(R.string.degree)));
            holder.tvTempMin.setText(data.getMinTemp().concat(context.getString(R.string.degree)));
        }
    }

    @Override
    public int getItemCount() {
        return dailyForecasts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_weekday)
        TextView tvWeekday;
        @BindView(R.id.iv_cond_day)
        ImageView ivCondDay;
        @BindView(R.id.tv_cond_day)
        TextView tvCondDay;
        @BindView(R.id.tv_temp_min)
        TextView tvTempMin;
        @BindView(R.id.tv_temp_max)
        TextView tvTempMax;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

/*    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_cond_day)
        ImageView ivCondDay;
        @BindView(R.id.tv_cond_day)
        TextView tvCondDay;
        @BindView(R.id.iv_cond_night)
        ImageView ivCondNight;
        @BindView(R.id.tv_cond_night)
        TextView tvCondNight;
        @BindView(R.id.tv_temp_max)
        TextView tvTempMax;
        @BindView(R.id.tv_temp_min)
        TextView tvTempMin;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }*/
}
