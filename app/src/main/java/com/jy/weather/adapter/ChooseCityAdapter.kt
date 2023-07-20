package com.jy.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jy.weather.R
import com.jy.weather.adapter.ChooseCityAdapter.ViewHolder

/**
 * Created by liyang on 2023/7/19
 * email: liyang4@yy.com
 */
class ChooseCityAdapter(
    private val data: List<Pair<String, String>>,
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_choose_city, parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCity.also {
            it.text = data[position].first
            it.setOnClickListener { onItemClick(data[position].first, data[position].second) }
            if (position == 0) {
                it.setCompoundDrawables(
                    ResourcesCompat.getDrawable(it.context.resources, R.drawable.ic_location, null),
                    null,
                    null,
                    null
                )
            }
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCity: TextView = view.findViewById(R.id.tv_city)
    }
}