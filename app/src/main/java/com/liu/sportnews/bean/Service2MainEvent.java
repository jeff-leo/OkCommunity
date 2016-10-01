package com.liu.sportnews.bean;

import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/8.
 */
public class Service2MainEvent {
    public int nowCommCount = 0;
    public List<MyCommentBean.Comments> alertCommList;
    public int flag = 0;//代表有提醒和无提醒时需要UI进行更新的标志
    public Service2MainEvent(int flag, int nowCommCount, List<MyCommentBean.Comments> alertCommList){
        this.flag = flag;
        this.nowCommCount = nowCommCount;
        this.alertCommList = alertCommList;
    }
}
