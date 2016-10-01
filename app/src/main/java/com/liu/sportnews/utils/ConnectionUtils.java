package com.liu.sportnews.utils;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 封装的网络数据加载工具，含有缓存功能
 */
public class ConnectionUtils {

    public static void getData(final Context context, final String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.print("Get方式:失败");
                EventBus.getDefault().post(Config.FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                CacheUtils.setCache(context, url, result);
                System.out.print("Get方式:成功/返回值" + result);

                EventBus.getDefault().post(result);
            }
        });
    }

    //Post方式传输登录注册收藏功能
    //如何优雅一点？？
    public static void postData(String url, FormBody body) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("失败");
                EventBus.getDefault().post(Config.FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                System.out.println("结果是" + result);
                //EventBus.getDefault().post();
            }
        });
    }

}
