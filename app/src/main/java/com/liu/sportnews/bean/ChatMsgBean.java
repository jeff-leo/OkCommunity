package com.liu.sportnews.bean;

import android.view.Menu;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Liujianfeng on 2016/7/26.
 */
public class ChatMsgBean extends MultiItemEntity{

    public static final int COMING = 0;
    public static final int SENDING = 1;

    public String info;
    public String date;
}
