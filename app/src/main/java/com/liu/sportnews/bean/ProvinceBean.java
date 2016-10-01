package com.liu.sportnews.bean;

import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/30.
 */
public class ProvinceBean {

    public List<Province> data;

    public class Province{
        public String areaName;
        public List<Cities> cities;
    }

    public class Cities{
        public String areaName;
    }
}
