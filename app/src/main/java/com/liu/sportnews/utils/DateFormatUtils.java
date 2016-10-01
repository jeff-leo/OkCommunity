package com.liu.sportnews.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 封装的日期计算工具类
 */
public class DateFormatUtils {

    /*
    获取时间差
     */
    public static String getTimesToNow(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String now = format.format(new Date());
        String returnText = null;
        try {
            long from = format.parse(date).getTime();
            long to = format.parse(now).getTime();
            int days = (int) ((to - from)/(1000 * 60 * 60 * 24));
            if(days == 0){//一天以内，以分钟或者小时显示
                int hours = (int) ((to - from)/(1000 * 60 * 60));
                if(hours == 0){
                    int minutes = (int) ((to - from)/(1000 * 60));
                    if(minutes == 0){
                        returnText = "刚刚";
                    }else{
                        returnText = minutes + "分钟前";
                    }
                }else{
                    returnText = hours + "小时前";
                }
            } else if(days == 1){
                returnText = "昨天";
            }else{
                returnText = days + "天前";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }
}
