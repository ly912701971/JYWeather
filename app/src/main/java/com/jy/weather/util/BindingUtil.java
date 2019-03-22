package com.jy.weather.util;

import android.databinding.BindingAdapter;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by liyang
 * on 2018/12/28
 */
public class BindingUtil {

    @BindingAdapter("imageId")
    public static void setImage(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter("backgroundId")
    public static void setBackground(ViewGroup viewGroup, int bgId) {
        viewGroup.setBackgroundResource(bgId);
    }
}