package com.jy.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.databinding.ItemSearchCityBinding
import com.jy.weather.navigator.ChooseCityNavigator
import com.jy.weather.viewmodel.CitySearchItemViewModel

/**
 * CitySearchAdapter
 *
 * Created by Yang on 2018/1/8.
 */
class CitySearchAdapter(
    context: Context,
    private val navigator: ChooseCityNavigator
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cityList: List<String> = listOf()

    override fun getCount(): Int {
        return cityList.size
    }

    override fun getItem(i: Int): String {
        return cityList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view = convertView
        val binding: ItemSearchCityBinding
        if (view == null) {
            binding = ItemSearchCityBinding.inflate(inflater, viewGroup, false)
            view = binding.root
        } else {
            binding = DataBindingUtil.getBinding(view)!!
        }

        val viewModel = CitySearchItemViewModel(getItem(i))
        viewModel.setNavigator(navigator)
        binding.viewModel = viewModel
        return view
    }

    fun setCityList(cityList: List<String>) {
        this.cityList = cityList
        notifyDataSetChanged()
    }
}
