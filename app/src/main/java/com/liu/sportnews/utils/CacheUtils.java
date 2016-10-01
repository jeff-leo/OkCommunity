package com.liu.sportnews.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
缓存工具,用于缓存新闻数据
 */
public class CacheUtils {

    public static void setCache(Context context, String url, String result){
        SharedPrerensUtils.setString(context, url, result);
    }

    public static String getCache(Context context, String url){
        return SharedPrerensUtils.getString(context, url);
    }
}
