package com.jy.weather.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.databinding.ItemCityBinding
import com.jy.weather.entity.CityData
import com.jy.weather.viewmodel.CityListItemViewModel

/**
 * 城市列表adapter
 *
 * Created by Yang on 2018/1/6.
 */
class CityListAdapter(context: Context) : BaseAdapter(), IBaseAdapter {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cityList: List<CityData> = listOf()

    override fun getCount() = cityList.size

    override fun getItem(i: Int) = cityList[i]

    override fun getItemId(i: Int) = i.toLong()

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view = convertView
        val binding: ItemCityBinding
        if (view == null) {
            binding = ItemCityBinding.inflate(inflater, viewGroup, false)
            view = binding.root
        } else {
            binding = DataBindingUtil.getBinding(view)!!
        }

        binding.viewModel = CityListItemViewModel(getItem(i))
        return view
    }

    override fun setData(data: List<*>) {
        cityList = data as List<CityData>
        notifyDataSetChanged()
    }
}
