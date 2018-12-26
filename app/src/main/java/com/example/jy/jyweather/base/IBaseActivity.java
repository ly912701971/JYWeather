package com.example.jy.jyweather.base;

/**
 * Activity基类接口
 * 封装了一些公用的方法
 * 泛型Data指该Activity所需数据类型
 *
 * @author Yang
 * @date 2018/9/4
 */
public interface IBaseActivity<Data> {

    /**
     * 显示错误信息
     */
    void showErrorMessage(String msg);

    /**
     * 显示数据
     *
     * @param data 从Presenter层取回的数据
     */
    void showData(Data data);
}
