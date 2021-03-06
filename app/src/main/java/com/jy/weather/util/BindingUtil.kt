package com.jy.weather.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.baoyz.swipemenulistview.SwipeMenuAdapter
import com.bumptech.glide.Glide
import com.jy.weather.R
import com.jy.weather.adapter.CommonAdapter
import com.jy.weather.adapter.IBaseAdapter

@BindingAdapter("imageId")
fun setImage(view: ImageView, resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter("imageUri")
fun setImageUri(view: ImageView, imageUri: String) {
    if (imageUri != "") {
        Glide.with(view.context)
            .load(imageUri)
            .placeholder(ColorDrawable(view.context.resources.getColor(R.color.background_dark)))
            .into(view)
    } else {
        view.setImageResource(R.drawable.ic_user_logout)
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