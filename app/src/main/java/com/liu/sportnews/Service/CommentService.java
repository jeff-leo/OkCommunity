package com.liu.sportnews.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.liu.sportnews.Broadcast.CommentBroadcast;
import com.liu.sportnews.bean.CommentBean;
import com.liu.sportnews.bean.MyCommentBean;
import com.liu.sportnews.bean.Service2MainEvent;
import com.liu.sportnews.utils.Config;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Liujianfeng on 2016/8/8.
 */
public class CommentService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "服务开启");
        getDataFromServer();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 2 * 60 * 1000;
        long triggerTime = SystemClock.elapsedRealtime() + time;//触发时间
        Intent i = new Intent(this, CommentBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getDataFromServer() {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_GET_ALERT_COMMENT)
                .addParams(Config.USERNAME, Config.login_name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("Server", response);
                        Gson gson = new Gson();
                        MyCommentBean bean = gson.fromJson(response, MyCommentBean.class);
                        int status = bean.status;
                        int nowCommCount = bean.nowCommCount;
                        Log.d("Server", nowCommCount+"");
                        if(status == 1) {
                            List<MyCommentBean.Comments> alertCommList = bean.comments;
                            EventBus.getDefault().post(new Service2MainEvent(1, nowCommCount, alertCommList));//需要更新
                        }else{
                            List<MyCommentBean.Comments> alertCommList = new ArrayList<>();
                            EventBus.getDefault().post(new Service2MainEvent(0, nowCommCount, alertCommList));//不需要更新
                        }
                    }
                });
    }
}
