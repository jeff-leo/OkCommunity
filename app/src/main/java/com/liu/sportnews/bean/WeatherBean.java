package com.liu.sportnews.bean;

import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/29.
 */
public class WeatherBean {

    public String reason;
    public ResultDetail result;

    public class ResultDetail{
        public Data data;
    }
    public class Data{
        public RealLife life;//生活指数
        public RealInfo realtime;//实时天气
        public List<WeakInfo> weather;//一周天气
    }

    public class RealLife{
        public LifeInfo info;//穿衣详情

        public class LifeInfo{
            public String[] chuanyi;
        }

    }

    public class RealInfo{
        public String city_name;//城市
        public String date;//日期
        public String time;//时间
        public DetailWeather weather;//具体温度
        public DetailWind wind;

        public class DetailWeather{
            public String info;//天气状况，如多云
            public String temperature;//度数
        }

        public class DetailWind{
            public String direct;//风向
            public String windspeed;//风力
        }
    }

    public class WeakInfo{
        public String week;
    }

}
