package com.jy.weather.util

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import com.baoyz.swipemenulistview.SwipeMenuAdapter
import com.bumptech.glide.Glide
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.adapter.IBaseAdapter

@BindingAdapter("imageId")
fun setImage(view: ImageView, resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter("imageUri")
fun setImageUri(view: ImageView, imageUri: String) {
    if (imageUri != "") {
        Glide.with(view.context).load(imageUri).into(view)
    }
}

@BindingAdapter("backgroundBitmap")
fun setBackgroundBitmap(viewGroup: ViewGroup, bitmap: Bitmap) {
    viewGroup.background = BitmapDrawable(viewGroup.context.resources, bitmap)
}

@BindingAdapter("backgroundId")
fun setBackground(viewGroup: ViewGroup, bgId: Int) {
    viewGroup.setBackgroundResource(bgId)
}

@BindingAdapter("data")
fun setData(listView: ListView, data: List<*>) {
    val adapter = if (listView.adapter is SwipeMenuAdapter) {
        (listView.adapter as SwipeMenuAdapter).wrappedAdapter
    } else {
        listView.adapter
    } as IBaseAdapter
    adapter.setData(data)
}

@BindingAdapter("refresh")
fun setRefresh(swipeRefreshLayout: SwipeRefreshLayout, refresh: Boolean) {
    swipeRefreshLayout.isRefreshing = refresh
}

@BindingAdapter("items")
fun setItems(recyclerView: RecyclerView, items: List<*>) {
    val adapter = recyclerView.adapter as CommonAdapter
    adapter.setItems(items)
}