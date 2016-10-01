package com.liu.sportnews.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Liujianfeng on 2016/7/16.
 */
public class CheckUtils {

    //检查用户名
    public static boolean checkUser(String user, Context context){
        if(user.length() < 5){
            Toast.makeText(context, "用户名不能少于5位", Toast.LENGTH_SHORT).show();
            return false;
        }else if(user.length() > 15){
            Toast.makeText(context, "用户名不能多于15位", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //检查密码
    public static boolean checkPass(String pass,  Context context){
        if(pass.length() < 6){
            Toast.makeText(context, "密码不能少于6位", Toast.LENGTH_SHORT).show();
            return false;
        }else if(pass.length() > 15){
            Toast.makeText(context, "用户名不能多于15位", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //检查确认密码是否一致
    public static boolean checkConPass(String pass, String confirPass,  Context context){
        if(!confirPass.equals(pass)){
            Toast.makeText(context, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //检查昵称
    public static boolean checkNick(String nick, Context context){
        if(nick.length() < 3){
            Toast.makeText(context, "昵称不能少于3位", Toast.LENGTH_SHORT).show();
            return false;
        }else if(nick.length() > 20){
            Toast.makeText(context, "昵称过长", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //检查个性签名
    public static boolean checkUnderWrite(String underWrite, Context context){
         if(underWrite.length() > 20) {
             Toast.makeText(context, "个性签名过长", Toast.LENGTH_SHORT).show();
             return false;
         }
        return true;
    }

    //将来用正则表达式
}
