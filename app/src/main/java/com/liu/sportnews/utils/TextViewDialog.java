package com.liu.sportnews.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Liujianfeng on 2016/7/28.
 */
public class TextViewDialog {

    private static String[] list = {"复制", "撤回", "删除"};
    private static Context mContext;

    public static void showDialog(final TextView view, final Context context){
        mContext = context;
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                manager.setText(view.getText());
                        }
                    }
                }).show();
                return false;
            }
        });
    }
}
