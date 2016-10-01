package com.liu.sportnews.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Liujianfeng
 */
public class WeChatBean {

    public int error_code;
    public String reason;
    public WeChatResult result;

    public class WeChatResult{
        public ArrayList<WeChatList> list;
        public int pno;
        public int ps;
        public int totalPage;
    }

    public class WeChatList implements Serializable{
        public String firstImg;
        public String id;
        public String source;
        public String title;
        public String url;
    }

}
