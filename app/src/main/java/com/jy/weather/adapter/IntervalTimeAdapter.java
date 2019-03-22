package com.jy.weather.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jy.weather.R;
import com.jy.weather.databinding.ItemIntervalTimeBinding;

/**
 * 间隔时间Adapter
 * <p>
 * Created by Yang on 2018/1/13.
 */
public class IntervalTimeAdapter extends BaseAdapter {

    private String[] intervalTimes;
    private LayoutInflater inflater;

    public IntervalTimeAdapter(Context context, String[] intervalTimes) {
        this.intervalTimes = intervalTimes;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return intervalTimes.length;
    }

    @Override
    public String getItem(int i) {
        return intervalTimes[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemIntervalTimeBinding binding;
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_interval_time, viewGroup, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (ItemIntervalTimeBinding) view.getTag();
        }

        binding.setIntervalTime(getItem(i));
        return view;
    }
}
