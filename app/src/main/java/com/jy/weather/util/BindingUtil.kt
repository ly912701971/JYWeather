package com.jy.weather.util

import android.databinding.BindingAdapter
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import com.baoyz.swipemenulistview.SwipeMenuAdapter
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
    if (data != null) {
        adapter.setData(data)
    }
}