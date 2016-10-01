package com.liu.sportnews.bean;

import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/9.
 */
public class Alert2MainEvent {
    public List<MyCommentBean.Comments> alertCommList;
    public Alert2MainEvent( List<MyCommentBean.Comments> alertCommList){
        this.alertCommList = alertCommList;
    }
}
