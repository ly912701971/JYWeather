package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.databinding.ItemSearchCityBinding
import com.jy.weather.viewmodel.CitySearchItemViewModel

/**
 * CitySearchAdapter
 *
 * Created by Yang on 2018/1/8.
 */
class CitySearchAdapter(context: Context) : BaseAdapter(), IBaseAdapter {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cityList: List<String> = listOf()

    override fun getCount() = cityList.size

    override fun getItem(i: Int) = cityList[i]

    override fun getItemId(i: Int) = i.toLong()

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view = convertView
        val binding: ItemSearchCityBinding
        if (view == null) {
            binding = ItemSearchCityBinding.inflate(inflater, viewGroup, false)
            view = binding.root
        } else {
            binding = DataBindingUtil.getBinding(view)!!
        }

        binding.viewModel = CitySearchItemViewModel(getItem(i))
        return view
    }

    override fun setData(data: List<*>) {
        cityList = data as List<String>
        notifyDataSetChanged()
    }
}
