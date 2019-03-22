package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.jy.weather.R
import com.jy.weather.databinding.ItemIntervalTimeBinding

/**
 * 间隔时间Adapter
 *
 *
 * Created by Yang on 2018/1/13.
 */
class IntervalTimeAdapter(
    context: Context,
    private val intervalTimes: Array<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return intervalTimes.size
    }

    override fun getItem(i: Int): String {
        return intervalTimes[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view = convertView
        val binding: ItemIntervalTimeBinding
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_interval_time, viewGroup, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemIntervalTimeBinding
        }

        binding.intervalTime = getItem(i)
        return view
    }
}
