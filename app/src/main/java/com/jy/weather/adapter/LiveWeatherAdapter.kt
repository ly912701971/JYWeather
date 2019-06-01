package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.BR
import com.jy.weather.R
import com.jy.weather.databinding.ItemLiveWeatherBinding
import com.jy.weather.entity.LiveWeather
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.viewmodel.LiveWeatherItemViewModel

class LiveWeatherAdapter(
    context: Context,
    private val navigator: LiveWeatherNavigator
) : BaseAdapter(), IBaseAdapter {

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

        binding.viewModel = LiveWeatherItemViewModel(getItem(position), navigator)
        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
            adapter = CommonAdapter(
                liveWeathers[position].commentArray,
                R.layout.item_comment,
                BR.comment
            )
        }
        return view
    }

    override fun setData(data: List<*>) {
        liveWeathers = data as List<LiveWeather>
        notifyDataSetChanged()
    }
}