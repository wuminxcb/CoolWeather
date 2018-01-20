package com.example.wumin.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/1/20 0020.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();  //http请求
        Request request = new Request.Builder().url(address).build(); //传入请求地址
        client.newCall(request).enqueue(callback);   //回调，处理服务器响应

    }
}
