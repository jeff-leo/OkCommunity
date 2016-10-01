package com.liu.sportnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.sportnews.utils.CheckUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.LoginLayout;
import com.liu.sportnews.utils.StatusUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class RegActivity extends AppCompatActivity {

    @BindView(R.id.name_wrap)
    TextInputLayout mNameWrap;

    @BindView(R.id.pass_wrap)
    TextInputLayout mPassWrap;

    @BindView(R.id.pass_confim_wrap)
    TextInputLayout mPassCfWrap;

    @BindView(R.id.username)
    EditText mUserName;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.password_confim)
    EditText mPasswordCf;

    @BindView(R.id.reg_btn)
    Button mRegBtn;

    @BindView(R.id.main_register)
    RelativeLayout mMainLayout;

    @BindView(R.id.reg_layout)
    LoginLayout mRegLayout;

    @BindView(R.id.return_login)
    TextView mReturn;

    @BindView(R.id.reg_progress)
    ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_reg);

        ButterKnife.bind(this);

        mNameWrap.setHint("请输入账号 (5-15位字母、数字)");
        mPassWrap.setHint("请输入密码 (6-15位字母、数字)");
        mPassCfWrap.setHint("请再输入一次密码密码");

        setRegBtnListener();//设置按钮
        setLayoutListener();//设置软键盘事件
    }

    private void setLayoutListener() {
        final LayoutParams params = (LayoutParams) mMainLayout.getLayoutParams();
        final int initMargin = params.topMargin;

        mRegLayout.setOnKeyBordListener(new LoginLayout.KeyBordListener() {
            @Override
            public void stateChange(int state) {
                switch (state) {
                    case LoginLayout.KEYBORD_SHOW:
                        params.topMargin = 50;
                        mMainLayout.setLayoutParams(params);
                        break;
                    case LoginLayout.KEYBORD_HIDE:
                        params.topMargin = initMargin;
                        mMainLayout.setLayoutParams(params);
                        break;
                }
            }
        });
    }

    private void setRegBtnListener() {

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mUserName.getText().toString();
                String pass = mPassword.getText().toString();
                String confirPass = mPasswordCf.getText().toString();

                //检查输入
                if (CheckUtils.checkUser(user, RegActivity.this) && CheckUtils.checkPass(pass, RegActivity.this)
                        && CheckUtils.checkConPass(pass, confirPass, RegActivity.this)) {
                    mProgress.setVisibility(View.VISIBLE);
                    OkHttpUtils.post()
                            .url(Config.LOCAL_URL)
                            .addParams(Config.ACTION, Config.ACTION_REGISTER)
                            .addParams("username", user)
                            .addParams("password", pass)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    mProgress.setVisibility(View.GONE);
                                    Toast.makeText(RegActivity.this, "网络异常，注册失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    mProgress.setVisibility(View.GONE);
                                    parseData(response);
                                }
                            });
                }
            }
        });
    }

    public void parseData(String result) {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(result);
            int status = jObject.getInt(Config.STATUS);
            if(status == 1){
                Toast.makeText(RegActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(RegActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
