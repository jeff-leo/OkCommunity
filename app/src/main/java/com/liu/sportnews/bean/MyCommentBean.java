package com.liu.sportnews.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/9.
 */
public class MyCommentBean {
    public int status;
    public int nowCommCount;
    public List<Comments> comments;

    public class Comments implements Serializable {
        public String nickname;
        public String headUrl;
        public String comment;
        public String sex;
        public String date;
        public String currentPos;
        public String secondCommId;
        public String timelineId;
        public TimeLineBean.TimelineInfo timeline;
    }
}
