package com.example.wumin.coolweather.gson;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;

        public String pm25;
    }
}
