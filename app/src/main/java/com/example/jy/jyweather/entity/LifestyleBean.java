package com.example.jy.jyweather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Lifestyle实体类
 * <p>
 * Created by Yang on 2017/10/15.
 */
public class LifestyleBean {

    private String type;    //类型

    @SerializedName("brf")
    private String brief;   //简要介绍

    @SerializedName("txt")
    private String text;    //详细信息

    public String getType() {
        return type;
    }

    public String getBrief() {
        return brief;
    }

    public String getText() {
        return text;
    }
}
