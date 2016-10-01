package com.liu.sportnews.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.liu.sportnews.Service.WeatherService;

public class WeatherBroadcast extends BroadcastReceiver {
    public WeatherBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, WeatherService.class);
        context.startService(i);

    }
}
