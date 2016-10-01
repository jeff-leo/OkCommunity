package com.liu.sportnews.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/14.
 */
public class ActivityCollectorUtils {

    private static List<Activity> activities = new ArrayList<>();

    public static void addActivites(Activity activity) {
        activities.add(activity);
    }

    public static void finishAll() {
        if(activities != null){
            for (Activity activity : activities) {
                activity.finish();
            }
        }

    }

}
