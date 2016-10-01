package com.liu.sportnews.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/8/3.
 */
public class InfoDBServerUtils {

    public static DatabaseHelper helper;

    public static void createDatabase(Context context){
        helper = new DatabaseHelper(context, "news.db", null, 1);
    }

    public static void initDatabase(String username){
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        database.insert("Info", null, values);
    }

    /*
    从数据库获取个人信息
     */
    public static List<String> getDataFromDatabase(){
        List<String> info = null;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query("Info", null, "username = ?", new String[]{Config.login_name}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                String sex = cursor.getString(cursor.getColumnIndex("sex"));
                String city = cursor.getString(cursor.getColumnIndex("city"));
                String underwrite = cursor.getString(cursor.getColumnIndex("underwrite"));
                String headUrl = cursor.getString(cursor.getColumnIndex("headUrl"));
                info = new ArrayList<>(Arrays.asList(username, nickname, sex, city, underwrite, headUrl));
                for(int i = 0; i < info.size()-1; i++){
                    if(TextUtils.isEmpty(info.get(i))){
                        info.set(i, "未填写");
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return info;
    }
}
