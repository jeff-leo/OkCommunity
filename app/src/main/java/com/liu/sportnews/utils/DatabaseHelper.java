package com.liu.sportnews.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liujianfeng on 2016/7/28.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String CREATE_CHAT = "create table ChatMessage (" +
            " id integer primary key autoincrement," +
            " username text," +
            " type integer," +
            " date text," +
            " content text)";

    public static final String CREATE_INFO = "create table Info" + "(" +
            " id integer primary key autoincrement, " +
            " username text," +
            " nickname text," +
            " sex text," +
            " city text," +
            " underwrite," +
            " headUrl)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT);
        db.execSQL(CREATE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
