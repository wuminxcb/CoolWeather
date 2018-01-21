package com.example.wumin.coolweather.gson;

import java.util.List;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    public List<Forecast> forecastList;
}
