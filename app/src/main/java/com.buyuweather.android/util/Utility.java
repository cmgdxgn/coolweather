package com.buyuweather.android.util;

import android.text.TextUtils;

import com.buyuweather.android.db.City;
import com.buyuweather.android.db.County;
import com.buyuweather.android.db.Province;
import com.buyuweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//用于解析和处理JSON数据的的类
public class Utility {
    //解析和处理服务器返回的省级数据，返回数据格式：{“id”：1，“name":"北京“}
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){ //如果返回数据不为空
            try {
                JSONArray allProvinces=new JSONArray(response);//利用返回的json数组构建json对象
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);//获取得到的json具体对象
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//litepal库会自动创建并存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据，返回数据格式：{“id”：113，“name":"南京“}
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){ //如果返回数据不为空
            try {
                JSONArray allCities=new JSONArray(response);//利用返回的json数组构建json对象
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);//获取得到的json具体对象
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);//存入对应的省级代码
                    city.save();//litepal库会自动创建并存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据，返回数据格式：{“id”：937，“name":"常州“，“weather_id":"CN101190401”}
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountryName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //将返回的JSON数据解析成Weather实体类
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            //因为返回的数据先是一个{}，需要用jasonobject来解析
            //{}里面又有[]，是一个数组，需要用JsonArray来解析
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);//将数据里符合Weather.class的项目填充进去
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
