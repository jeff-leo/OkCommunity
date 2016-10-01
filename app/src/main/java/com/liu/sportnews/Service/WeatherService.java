package com.liu.sportnews.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.liu.sportnews.Broadcast.WeatherBroadcast;
import com.liu.sportnews.utils.CacheUtils;
import com.liu.sportnews.utils.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class WeatherService extends Service {
    public WeatherService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getDataFromServer();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 60 * 60 * 1000;//刷新时间
        long triggerTime = SystemClock.elapsedRealtime() + hour;
        Intent i = new Intent(this, WeatherBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getDataFromServer() {
        String city = CacheUtils.getCache(this, Config.CITY);
        String url = null;
        if(city != null){
            url = Config.WEATHER_URL + city + Config.WEATHER_KEY;
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(WeatherService.this, "无服务", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("service", response);
                            CacheUtils.setCache(WeatherService.this, Config.CITY_WEATHER, response);
                        }
                    });
        }
    }
}
