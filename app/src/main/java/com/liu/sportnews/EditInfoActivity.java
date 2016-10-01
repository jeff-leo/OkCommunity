package com.liu.sportnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.sportnews.utils.CheckUtils;
import com.liu.sportnews.utils.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class EditInfoActivity extends AppCompatActivity {

    @BindView(R.id.sureBtn)
    RelativeLayout mSureBtn;
    @BindView(R.id.edit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.infoText)
    EditText mInfoEdit;
    @BindView(R.id.hihtText)
    TextView mHihtText;

    private String mInfo;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String title = bundle.getString("title");
        String hint = bundle.getString("hint");
        mType = bundle.getString("type");

        if(title != null && hint != null){
            mToolbar.setTitle(title);
            mHihtText.setText(hint);
        }

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfo = mInfoEdit.getText().toString();
                if(mType.equals("nickname")){
                    if(CheckUtils.checkNick(mInfo, EditInfoActivity.this)){
                        upLoadInfoToServer(mType, mInfo);
                    }
                }else{
                    if(CheckUtils.checkUnderWrite(mInfo, EditInfoActivity.this)){
                        upLoadInfoToServer(mType, mInfo);
                    }
                }

            }
        });
    }

    public void upLoadInfoToServer(String type, String info){
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_UPLOAD_INFO)
                .addParams(Config.USERNAME, Config.login_name)
                .addParams("type", type)
                .addParams("info", info)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(EditInfoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jObject;
                        int status = 0;
                        try {
                            jObject = new JSONObject(response);
                            status = jObject.getInt(Config.STATUS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (status == 1) {
                            Toast.makeText(EditInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditInfoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}


