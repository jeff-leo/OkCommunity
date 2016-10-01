package com.liu.sportnews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Liujianfeng on 2016/7/12.
 */
public class SharedPrerensUtils {

    public static void setString(Context context, String key, String content){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, content).commit();
    }

    public static String getString(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    //设置登录状态
    public static void setBoolean(Context context, String key, Boolean content){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, content).commit();
    }

    public static Boolean getBoolean(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /*
    清除sp，在注销时调用
     */
    public static void clearSp(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear().commit();
    }

    public static void setInt(Context context, String key, int content){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY,Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, content).commit();
    }

    public static int getInt(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_KEY, Context.MODE_PRIVATE);
       return sp.getInt(key, 0);
    }

}
