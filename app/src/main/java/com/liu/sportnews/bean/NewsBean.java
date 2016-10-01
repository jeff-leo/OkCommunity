package com.liu.sportnews.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/10.
 */
public class NewsBean {

    public int error_code;
    public String reason;

    public NewsList result;

    public class NewsList{
        public String start;
        public ArrayList<NewsDetailList> data;
    }

    //使用Serializable序列化的方式传递，最好使用Parcelable
    public class NewsDetailList implements Serializable{
        public String author_name;
        public String date;
        public String realtype;//真正所属的类型
        public String thumbnail_pic_s;//图片链接
        public String thumbnail_pic_s02;
        public String thumbnail_pic_s03;
        public String title;
        public String type;//搜索的类型
        public String uniquekey;
        public String url;//详情链接
    }


}
