package com.example.wumin.coolweather.gson;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class Suggestion {
    public Comfort comfort;
    public CarWash carWash;
    public Sport sport;

    public class Comfort {
        public String info;
    }

    public class CarWash {
        public String info;
    }

    public class Sport {
        public String info;
    }

}
