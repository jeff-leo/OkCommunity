package com.liu.sportnews.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/31.
 */
public class CollectsBean {

    public class Collects{
        public int status;
        public List<CollectsList> collections;
    }

    //使用Serializable序列化的方式传递，最好使用Parcelable
    public class CollectsList implements Serializable {
        public String date;
        public String thumbnail_pic_s;//图片链接
        public String thumbnail_pic_s02;
        public String thumbnail_pic_s03;
        public String title;
        public String uniquekey;
        public String url;//详情链接
    }

}
