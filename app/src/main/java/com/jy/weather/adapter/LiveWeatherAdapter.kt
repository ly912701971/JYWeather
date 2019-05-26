package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.databinding.ItemLiveWeatherBinding
import com.jy.weather.entity.LiveWeather
import com.jy.weather.viewmodel.LiveWeatherItemViewModel

class LiveWeatherAdapter(context: Context) : BaseAdapter(), IBaseAdapter {

    private val inflater = LayoutInflater.from(context)
    private var liveWeathers: List<LiveWeather> = listOf()

    override fun getItem(position: Int) = liveWeathers[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = liveWeathers.size

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        var view = convertView
        val binding: ItemLiveWeatherBinding
        if (view == null) {
            binding = ItemLiveWeatherBinding.inflate(inflater, viewGroup, false)
            view = binding.root
        } else {
            binding = DataBindingUtil.getBinding(view)!!
        }

        binding.viewModel = LiveWeatherItemViewModel(getItem(position))
        return view
    }

    override fun setData(data: List<*>) {
        liveWeathers = data as List<LiveWeather>
        notifyDataSetChanged()
    }
}