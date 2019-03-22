package com.jy.weather.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.activity.WeatherActivity
import com.jy.weather.databinding.ItemSearchCityBinding
import java.util.*

/**
 * CitySearchAdapter
 *
 *
 * Created by Yang on 2018/1/8.
 */
class CitySearchAdapter(
    context: Context,
    private val cityList: List<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

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
            binding = DataBindingUtil.inflate(inflater, R.layout.item_search_city, viewGroup, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemSearchCityBinding
        }

        binding.cityInfo = getItem(i)
        binding.tvCityInfo.setOnClickListener {
            val citySet = HashSet(JYApplication.cityDB.citySet)
            val city = cityList[i].split(" - ".toRegex()).getOrElse(0) { "unknown" }
            if (!citySet.contains(city)) {
                citySet.add(city)
                JYApplication.cityDB.citySet = citySet
                if (citySet.size == 1) {
                    JYApplication.cityDB.defaultCity = city
                }
            }

            val intent = Intent(JYApplication.INSTANCE, WeatherActivity::class.java)
            intent.putExtra("city", city)
            JYApplication.INSTANCE.startActivity(intent)
        }
        return view
    }
}
