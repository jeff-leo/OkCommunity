package com.liu.sportnews;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liu.sportnews.Service.CommentService;
import com.liu.sportnews.bean.Alert2MainEvent;
import com.liu.sportnews.bean.InfoBean;
import com.liu.sportnews.bean.MyCommentBean;
import com.liu.sportnews.bean.Service2MainEvent;
import com.liu.sportnews.fragment.basefragment.CollectFragment;
import com.liu.sportnews.fragment.basefragment.HomeFragment;
import com.liu.sportnews.utils.ActivityCollectorUtils;
import com.liu.sportnews.utils.CheckVersion;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.InfoDBServerUtils;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.content_drawer)
    DrawerLayout mContent_drawer;
    @BindView(R.id.navigation_id)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.appBar)
    AppBarLayout mAppBar;
    @BindView(R.id.alert)
    RelativeLayout mAlertBtn;
    @BindView(R.id.alert_btn)
    ImageView mAlertIb;

    private ActionBarDrawerToggle toogle;

    //navigation的头布局
    private View mHeaderView;
    private Button mBtnLogin;
    private CircleImageView mHeaderImage;

    private FragmentManager mManager;

    private long mCurrentTime;

    //Service传来的提醒数据
    private List<MyCommentBean.Comments> mAlertCommList = new ArrayList<>();
    private int mNowCommCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollectorUtils.addActivites(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Logger.init("MainActivity");
        InfoDBServerUtils.createDatabase(getApplicationContext());//创建数据库

        //设置toolbar
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

        //设置滑动的toogle
        toogle = new ActionBarDrawerToggle(this, mContent_drawer, mToolbar,
                R.string.open, R.string.close);
        mContent_drawer.setDrawerListener(toogle);
        toogle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mManager = getSupportFragmentManager();

        //更新界面数据
        UpdateUIFromLogin();
        CheckVersion.checkVersion(this, Config.MAIN);
        changeFragment(HomeFragment.newInstance(this, mManager));

        startService(new Intent(MainActivity.this, CommentService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String imageUrl = SharedPrerensUtils.getString(this, Config.HEAD_URL_KEY);
        String nickname = SharedPrerensUtils.getString(this, Config.NICKNAME_KEY);
        if(Config.login_status){
            parseData(imageUrl, nickname);
            getInfoFromServer();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEvent(Service2MainEvent event) {
        int flag = event.flag;
        if(flag == 1){//需要更新
            mAlertIb.setImageResource(R.drawable.message2);
        }
        mAlertCommList = event.alertCommList;
        mNowCommCount = event.nowCommCount;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlertEvent(Alert2MainEvent event) {
        mAlertCommList = event.alertCommList;
        mAlertIb.setImageResource(R.drawable.message);
    }

    public void getInfoFromServer() {
        if (SharedPrerensUtils.getBoolean(this, Config.ISLOGIN)) {
            OkHttpUtils.post()
                    .url(Config.LOCAL_URL)
                    .addParams(Config.ACTION, Config.ACTION_SEARCH_INFO)
                    .addParams(Config.USERNAME, Config.login_name)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (response != null) {
                                Gson gson = new Gson();
                                InfoBean mInfoBean = gson.fromJson(response, InfoBean.class);
                                initDatabase(mInfoBean.nickname, mInfoBean.sex, mInfoBean.city, mInfoBean.underwrite, mInfoBean.headUrl);
                                String imageUrl = mInfoBean.headUrl;
                                String nickname = mInfoBean.nickname;
                                String sex = mInfoBean.sex;
                                if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(nickname)) {
                                    parseData(imageUrl, nickname);
                                    SharedPrerensUtils.setString(MainActivity.this, Config.NICKNAME_KEY, nickname);
                                    SharedPrerensUtils.setString(MainActivity.this, Config.HEAD_URL_KEY, imageUrl);
                                    SharedPrerensUtils.setString(MainActivity.this, Config.SEX_KEY, sex);
                                }
                            }
                        }
                    });
        }
    }

    private void initDatabase(String ... params) {
        SQLiteDatabase database = InfoDBServerUtils.helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", params[0]);
        values.put("sex", params[1]);
        values.put("city", params[2]);
        values.put("underwrite", params[3]);
        values.put("headUrl", params[4]);
        database.update("Info", values, "username = ?", new String[]{Config.login_name});
    }

    public void parseData(String imageUrl, String nickname) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .crossFade()
                    .into(mHeaderImage);
        }
        if (!TextUtils.isEmpty(nickname)) {
            mBtnLogin.setText(nickname);
        }
    }

    //实现双击两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mCurrentTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mCurrentTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;//如果是后退键，则截获动作
        }
        return super.onKeyDown(keyCode, event);
    }

    public void UpdateUIFromLogin() {
        mHeaderView = mNavigationView.getHeaderView(0);
        mBtnLogin = (Button) mHeaderView.findViewById(R.id.main_login);
        mHeaderImage = (CircleImageView) mHeaderView.findViewById(R.id.user_image);

        //获取登录标志
        Config.login_status = SharedPrerensUtils.getBoolean(this, Config.ISLOGIN);
        if (Config.login_status) {
            //登录成功就把sharedPreference中保存的用户名取出来保存到Config中,用于之后程序使用
            Config.login_name = SharedPrerensUtils.getString(this, Config.USERNAME);
            mBtnLogin.setText("未填写昵称");
        }

        //点击登录
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Config.login_status) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        mAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.login_status) {
                    Intent intent = new Intent(MainActivity.this, AlertActivity.class);
                    intent.putExtra("commList", (Serializable) mAlertCommList);
                    intent.putExtra("nowCommCount", mNowCommCount);
                    intent.putExtra("from", "Main");
                    Logger.d("BtnEvent" + mNowCommCount);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //切换fragment
    public void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = mManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        mContent_drawer.closeDrawer(GravityCompat.START);
        switch (id) {
            case R.id.myhome:
                changeFragment(HomeFragment.newInstance(this, mManager));
                mToolbar.setTitle("主页");
                break;
            case R.id.faviorite:
                if (!Config.login_status) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    changeFragment(CollectFragment.newInstance(this));
                    mToolbar.setTitle("收藏");
                }
                break;
            case R.id.exit:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("注销登陆")
                        .setMessage("是否注销？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPrerensUtils.clearSp(MainActivity.this);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                ActivityCollectorUtils.finishAll();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNavigationView.setCheckedItem(R.id.myhome);
                    }
                }).show();
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                mNavigationView.setCheckedItem(R.id.myhome);
                break;
            case R.id.about:
                String versionName = null;
                try {
                    versionName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                dialog.setTitle("ok社区 Version" + versionName)
                        .setMessage("做大家的新闻社区")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNavigationView.setCheckedItem(R.id.myhome);
                            }
                        }).show();
                break;
            default:
                break;
        }
        return true;
    }

    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

}
