package com.jy.weather.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CommonAdapter(
    private var dataList: List<*>,
    private val layoutId: Int,
    private val dataId: Int
) : RecyclerView.Adapter<CommonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommonAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(viewGroup.context),
            layoutId,
            viewGroup,
            false
        )
        return ViewHolder(binding.root).apply {
            this.binding = binding
        }
    }

    override fun onBindViewHolder(viewHolder: CommonAdapter.ViewHolder, i: Int) {
        viewHolder.binding.apply {
            setVariable(dataId, dataList[i])
            executePendingBindings()// 防止闪烁
        }
    }

    override fun getItemCount() = dataList.size

    fun setItems(items: List<*>) {
        dataList = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ViewDataBinding
    }
}
