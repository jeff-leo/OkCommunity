package com.liu.sportnews.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by Liujianfeng on 2016/7/27.
 */
public class LoginLayout extends LinearLayout{

    public static final int KEYBORD_SHOW = 0;
    public static final int KEYBORD_HIDE = 1;

    public interface KeyBordListener{
        void stateChange(int state);
    }

    public KeyBordListener mKeyBordListener;

    public void setOnKeyBordListener(KeyBordListener keyBordListener){
        mKeyBordListener = keyBordListener;
    }

    public LoginLayout(Context context) {
        super(context);
    }

    public LoginLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler handler = new Handler();

    /*
     当布局位置变化时监听
     */
    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d("onSizeChanged", oldh + "//" + h);
        //这里为什么要开线程？？
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(mKeyBordListener != null){
                    if(oldh - h > 0){
                        mKeyBordListener.stateChange(KEYBORD_SHOW);
                    }else{
                        mKeyBordListener.stateChange(KEYBORD_HIDE);
                    }
                }
            }
        });

    }
}
