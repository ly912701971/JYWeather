package com.jy.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class CommonAdapter(
    private var dataList: List<*>,
    private val layoutId: Int,
    private val dataId: Int
) : RecyclerView.Adapter<CommonAdapter.ViewHolder>() {

    private var listener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
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

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.binding.apply {
            listener?.let { l ->
                root.setOnClickListener {
                    l.invoke(i)
                }
            }
            setVariable(dataId, dataList[i])
            executePendingBindings()// 防止闪烁
        }
    }

    override fun getItemCount() = dataList.size

    fun setItems(items: List<*>) {
        dataList = items
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ViewDataBinding
    }
}
