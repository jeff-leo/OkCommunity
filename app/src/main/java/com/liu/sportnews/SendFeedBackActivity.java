package com.liu.sportnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.sportnews.bean.TimeLineEvent;
import com.liu.sportnews.utils.Config;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SendFeedBackActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.publish)
    RelativeLayout mPublish;
    @BindView(R.id.timelineText)
    EditText mText;
    @BindView(R.id.publish_text)
    TextView mTextView;

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

        mText.setHint("说说对ok社区app的意见");
        mTextView.setText("发送");
        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mText.getText().toString();
                OkHttpUtils.post()
                        .url(Config.LOCAL_URL)
                        .addParams(Config.ACTION, Config.ACTION_ADD_FEED_BACK)
                        .addParams(Config.USERNAME, Config.login_name)
                        .addParams("feedback", text)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(SendFeedBackActivity.this, "网络异常，发送失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    int status = jObject.getInt(Config.STATUS);
                                    if(status == 1){
                                        Toast.makeText(SendFeedBackActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(SendFeedBackActivity.this, "网络异常，发送失败", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        });
            }
        });
    }
}
