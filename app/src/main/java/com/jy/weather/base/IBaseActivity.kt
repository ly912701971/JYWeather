package com.jy.weather.base

/**
 * Activity基类接口
 * 封装了一些公用的方法
 * 泛型Data指该Activity所需数据类型
 *
 * @author Yang
 * @date 2018/9/4
 */
interface IBaseActivity<Data> {

    /**
     * 显示错误信息
     */
    fun showErrorMessage(msg: String)

    /**
     * 显示数据
     *
     * @param data 从Presenter层取回的数据
     */
    fun showData(data: Data)
}
