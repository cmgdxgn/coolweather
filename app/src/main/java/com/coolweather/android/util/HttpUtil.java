package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//新建了服务器交互类，发起HTTP请求只需要传入地址并注册回调来处理服务器响应即可
public class HttpUtil {
    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
