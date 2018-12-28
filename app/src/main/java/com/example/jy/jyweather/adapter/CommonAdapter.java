package com.example.jy.jyweather.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by liyang
 * on 2018/12/28
 */
public class CommonAdapter<T> extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {

    private List<T> dataList;
    private int layoutId;
    private int dataId;

    public CommonAdapter(List<T> mDatas, int layoutId, int dataId) {
        this.dataList = mDatas;
        this.layoutId = layoutId;
        this.dataId = dataId;
    }

    @NonNull
    @Override
    public CommonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                layoutId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonAdapter.ViewHolder viewHolder, int i) {
        viewHolder.getBinding().setVariable(dataId, dataList.get(i));
        viewHolder.getBinding().executePendingBindings();// 防止闪烁
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ViewDataBinding getBinding() {
            return binding;
        }

        void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }

}
