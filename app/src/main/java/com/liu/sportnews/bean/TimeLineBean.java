package com.liu.sportnews.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/5.
 */
public class TimeLineBean {
    public int status;
    public List<TimelineInfo> timeline;

    public class TimelineInfo implements Serializable{
        public int timelineId;
        public String username;
        public String content;
        public String date;
        public String nickname;
        public String sex;
        public String city;
        public String headUrl;
        public int cmCount;
    }
}
