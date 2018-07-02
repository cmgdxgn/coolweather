package com.buyuweather.android.gson;

import com.buyuweather.android.gson.Aqi;
import com.buyuweather.android.gson.Basic;
import com.buyuweather.android.gson.Now;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//新建一个总的实例类调用json对应的各个实体类
public class Weather {
    public String status;

    public Basic basic;

    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;
    //用集合来存储daily_forecast里面多天的天气
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
