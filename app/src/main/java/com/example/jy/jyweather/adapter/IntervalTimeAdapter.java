package com.example.jy.jyweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jy.jyweather.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 间隔时间Adapter
 * <p>
 * Created by Yang on 2018/1/13.
 */

public class IntervalTimeAdapter extends BaseAdapter {

    private Context context;
    private String[] intervalTimes;

    public IntervalTimeAdapter(Context context, String[] intervalTimes) {
        this.context = context;
        this.intervalTimes = intervalTimes;
    }

    @Override
    public int getCount() {
        return intervalTimes.length;
    }

    @Override
    public Object getItem(int i) {
        return intervalTimes[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_interval_time, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvTime.setText(intervalTimes[i]);
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
