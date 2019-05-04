package com.jy.weather.util

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import com.baoyz.swipemenulistview.SwipeMenuAdapter
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.adapter.IBaseAdapter

/**
 * Created by liyang
 * on 2018/12/28
 */
@BindingAdapter("imageId")
fun setImage(view: ImageView, resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter("backgroundId")
fun setBackground(viewGroup: ViewGroup, bgId: Int) {
    viewGroup.setBackgroundResource(bgId)
}

@BindingAdapter("data")
fun setData(listView: ListView, data: List<*>?) {
    val adapter = if (listView.adapter is SwipeMenuAdapter) {
        (listView.adapter as SwipeMenuAdapter).wrappedAdapter
    } else {
        listView.adapter
    } as IBaseAdapter
    adapter.setData(data ?: return)
}

@BindingAdapter("refresh")
fun setRefresh(swipeRefreshLayout: SwipeRefreshLayout, refresh: Boolean) {
    swipeRefreshLayout.isRefreshing = refresh
}

@BindingAdapter("items")
fun <T> setItems(recyclerView: RecyclerView, items: List<T>) {
    val adapter = recyclerView.adapter as CommonAdapter<T>
    adapter.setItems(items)
}