package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.databinding.ItemCityBinding

/**
 * 城市列表adapter
 *
 *
 * Created by Yang on 2018/1/6.
 */
class CityListAdapter(
    context: Context,
    private val cityList: List<Map<String, String>>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return cityList.size
    }

    override fun getItem(i: Int): Map<String, String> {
        return cityList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view = convertView
        val defaultCity = JYApplication.cityDB.defaultCity
        val binding: ItemCityBinding
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_city, viewGroup, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemCityBinding
        }

        binding.map = getItem(i)
        binding.defaultName = defaultCity
        return view
    }
}
