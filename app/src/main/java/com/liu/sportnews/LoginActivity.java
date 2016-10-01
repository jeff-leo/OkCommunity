package com.liu.sportnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.sportnews.utils.CheckUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.InfoDBServerUtils;
import com.liu.sportnews.utils.LoginLayout;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.liu.sportnews.utils.StatusUtil;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity{

    @BindView(R.id.name_wrap)
    TextInputLayout userWrap;

    @BindView(R.id.pass_wrap)
    TextInputLayout passWrap;

    @BindView(R.id.login_btn)
    Button mLoginBtn;

    @BindView(R.id.username)
    EditText mUserName;

    @BindView(R.id.password)
    EditText mPassWord;

    private String mUser;
    private String mPass;

    @BindView(R.id.regiter_btn)
    TextView mRegBtn;

    @BindView(R.id.login)
    LoginLayout mRootLayout;

    @BindView(R.id.main_login)
    RelativeLayout mMainLayout;

    @BindView(R.id.login_progress)
    ProgressBar mProgress;

    private InputMethodManager mInputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_login);
        Logger.init("Login");
        ButterKnife.bind(this);

        userWrap.setHint("请输入账号");
        passWrap.setHint("请输入密码");

        setKeyBord();

        mInputManager = (InputMethodManager) mLoginBtn.getContext().getSystemService(INPUT_METHOD_SERVICE);
        setListener();
    }

    private void setKeyBord() {
        final LayoutParams params = (LayoutParams) mMainLayout.getLayoutParams();
        final int initMargin = params.topMargin;
        mRootLayout.setOnKeyBordListener(new LoginLayout.KeyBordListener() {
            @Override
            public void stateChange(int state) {
                switch (state) {
                    case LoginLayout.KEYBORD_SHOW:
                        //这里不应该限死，应该动态获取
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

    private void setListener() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser = mUserName.getText().toString();
                mPass = mPassWord.getText().toString();
                if (CheckUtils.checkUser(mUser, LoginActivity.this) && CheckUtils.checkPass(mPass, LoginActivity.this)) {
                    mProgress.setVisibility(View.VISIBLE);
                    OkHttpUtils.post()
                            .url(Config.LOCAL_URL)
                            .addParams(Config.ACTION, Config.ACTION_LOGIN)
                            .addParams("username", mUser)
                            .addParams("password", mPass)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    mProgress.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "网络异常，登录失败", Toast.LENGTH_SHORT).show();
                                    mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    mProgress.setVisibility(View.GONE);
                                    parseData(response);
                                }
                            });
                }else{
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegActivity.class));
            }
        });
    }

    public void parseData(String result) {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(result);
            int status = jObject.getInt(Config.STATUS);
            int date = jObject.getInt(Config.REGISTE_DATE);
            ++date;
            if(status == 1){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                SharedPrerensUtils.setBoolean(this, Config.ISLOGIN, true);
                SharedPrerensUtils.setString(this, Config.USERNAME, mUser);
                SharedPrerensUtils.setString(this, Config.REGISTE_DATE, Integer.toString(date));
                //注册成功创建app需要的数据库和表
                InfoDBServerUtils.createDatabase(getApplicationContext());
                InfoDBServerUtils.initDatabase(mUser);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
