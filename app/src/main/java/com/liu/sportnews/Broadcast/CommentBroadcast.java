package com.liu.sportnews.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.liu.sportnews.Service.CommentService;

public class CommentBroadcast extends BroadcastReceiver {
    public CommentBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CommentService.class);
        context.startService(i);
    }
}
