package com.liu.sportnews;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.sportnews.bean.TimeLineEvent;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class PublishTLActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.publish)
    RelativeLayout mPublish;
    @BindView(R.id.timelineText)
    EditText mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_tl);
        ButterKnife.bind(this);

        mToolbar.setTitle("取消");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TimeLineEvent(0));
                finish();
            }
        });

        boolean entered = SharedPrerensUtils.getBoolean(PublishTLActivity.this, "entered");//第一次进入时才显示dailog
        if(!entered){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("注意")
                    .setMessage("为保证社区质量，请不要发表包含不良信息的帖子")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPrerensUtils.setBoolean(PublishTLActivity.this, "entered", true);
                        }
                    }).show();
        }
        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mText.getText().toString();
                OkHttpUtils.post()
                        .url(Config.LOCAL_URL)
                        .addParams(Config.ACTION, Config.ACTION_ADD_TIMELINE)
                        .addParams(Config.USERNAME, Config.login_name)
                        .addParams("content", text)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(PublishTLActivity.this, "网络异常，发布失败", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new TimeLineEvent(1));
                                finish();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    int status = jObject.getInt(Config.STATUS);
                                    if (status == 1) {
                                        Toast.makeText(PublishTLActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PublishTLActivity.this, "网络异常，发布失败", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                EventBus.getDefault().post(new TimeLineEvent(1));
                                finish();
                            }
                        });
            }
        });
    }
}
