package com.liu.sportnews.bean;

import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/9.
 */
public class PictureBean {
    public int status;
    public Picture pictures;

    public class Picture{
        public List<Pic> data;
    }
    public class Pic{
        public String first_pic;
        public String[] pics;
        public String title;
    }
}
