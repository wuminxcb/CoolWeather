package com.example.wumin.coolweather.gson;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class Forecast {
    public String date;

    public Temperature temperature;

    public More more;

    private class Temperature {
        public String max;
        public String min;
    }

    private class More {
        public String info;
    }
}
