package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.liu.sportnews.bean.CollectsBean.CollectsList;
import com.liu.sportnews.bean.EventCollect;
import com.liu.sportnews.bean.NewsBean.NewsDetailList;
import com.liu.sportnews.bean.WeChatBean.WeChatList;
import com.liu.sportnews.fragment.basefragment.CollectFragment;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.liu.sportnews.utils.StatusUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class NewsDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_webview)
    WebView mWebView;
    @BindView(R.id.collapsing_bar)
    CollapsingToolbarLayout mCtbLayout;
    @BindView(R.id.detail_image)
    ImageView mImage;
    @BindView(R.id.detail_fab)
    FloatingActionButton mFab;
    @BindView(R.id.detail_pb)
    ProgressBar mProgressBar;

    private Context mContext;

    private NewsDetailList itemData;//新闻数据
    private WeChatList weChatItemData;//微信精选
    private CollectsList collectsData;//收藏数据

    /*
    传过来的数据
     */
    private String mNewsUrl;
    private String mImageUrl2;
    private String mTitle;
    private String mDate;
    private String mImageUrl;

    private static final String SELECTED = "1";
    private static final String UNSELECTED = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        mContext = this;
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();
        setSelected();
        setListener();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mWebView.loadUrl(mNewsUrl);
    }

    private void initData() {
        /*
        分类出是新闻还是微信精选或收藏
         */
        itemData = (NewsDetailList) getIntent().getSerializableExtra(Config.NEWS_DATA);
        if (itemData != null) {
            mNewsUrl = itemData.url;
            mImageUrl2 = itemData.thumbnail_pic_s03;
            mTitle = itemData.title;
            mDate = itemData.date;
            mImageUrl = itemData.thumbnail_pic_s;
        } else {
            weChatItemData = (WeChatList) getIntent().getSerializableExtra(Config.WECHAT_DATA);
            if (weChatItemData != null) {
                mNewsUrl = weChatItemData.url;
                mImageUrl2 = weChatItemData.firstImg;
                mTitle = weChatItemData.source;
                mDate = weChatItemData.source;
                mImageUrl = mImageUrl2;
            } else {
                collectsData = (CollectsList) getIntent().getSerializableExtra(Config.COLLECT_DATA);
                if (collectsData != null) {
                    mNewsUrl = collectsData.url;
                    mImageUrl2 = collectsData.thumbnail_pic_s03;
                    mTitle = collectsData.title;
                    mDate = collectsData.date;
                    mImageUrl = collectsData.thumbnail_pic_s;
                }
            }
        }

        //设置CollapsingToolbarLayout的信息
        mCtbLayout.setTitle(mTitle);
        mCtbLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCtbLayout.setExpandedTitleColor(Color.WHITE);
        mCtbLayout.setExpandedTitleMarginStart(40);
        mCtbLayout.setExpandedTitleMarginBottom(30);

        Glide.with(this)
                .load(mImageUrl2)
                .centerCrop()
                .crossFade()
                .into(mImage);
    }

    private void setSelected() {
        //访问服务器判断此连接是否已经收藏
        String isSelect = SharedPrerensUtils.getString(mContext, mNewsUrl);
        if (TextUtils.isEmpty(isSelect)) {
            OkHttpUtils.post()
                    .url(Config.LOCAL_URL)
                    .addParams(Config.ACTION, Config.ACTION_SEARCH_COLLECTS)
                    .addParams("username", Config.login_name)
                    .addParams("url", mNewsUrl)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            JSONObject jObject;
                            int status = 0;
                            try {
                                jObject = new JSONObject(response);
                                status = jObject.getInt("status");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (status == 1) {
                                SharedPrerensUtils.setString(mContext, mNewsUrl, SELECTED);
                                mFab.setSelected(true);
                            } else {
                                SharedPrerensUtils.setString(mContext, mNewsUrl, UNSELECTED);
                                mFab.setSelected(false);
                            }
                        }
                    });
        } else {
            if (isSelect.equals(SELECTED)) {
                mFab.setSelected(true);
            } else {
                mFab.setSelected(false);
            }
        }
    }

    private void setListener() {
        /*
        点击收藏,或单击取消收藏
         */
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFab.isSelected()) {
                    OkHttpUtils.post()
                            .url(Config.LOCAL_URL)
                            .addParams(Config.ACTION, Config.ACTION_COLLECTS)
                            .addParams("username", Config.login_name)
                            .addParams("date", mDate)
                            .addParams("title", mTitle)
                            .addParams("picurl", mImageUrl)
                            .addParams("picurl2", mImageUrl2)
                            .addParams("url", mNewsUrl)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Snackbar.make(mCtbLayout, "网络异常", Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    JSONObject jsonObject;
                                    int status = 0;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        status = jsonObject.getInt("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status == 1) {
                                        mFab.setSelected(true);
                                        Snackbar.make(mCtbLayout, "收藏成功", Snackbar.LENGTH_SHORT).show();
                                        SharedPrerensUtils.setString(mContext, mNewsUrl, SELECTED);
                                    } else {
                                        Snackbar.make(mCtbLayout, "网络异常", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    OkHttpUtils.post()
                            .url(Config.LOCAL_URL)
                            .addParams(Config.ACTION, Config.ACTION_DELETE_COLLECTS)
                            .addParams("username", Config.login_name)
                            .addParams("url", mNewsUrl)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Snackbar.make(mCtbLayout, "网络异常", Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    JSONObject jsonObject;
                                    int status = 0;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        status = jsonObject.getInt("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (status == 1) {
                                        mFab.setSelected(false);
                                        Snackbar.make(mCtbLayout, "取消成功", Snackbar.LENGTH_SHORT).show();
                                        SharedPrerensUtils.setString(mContext, mNewsUrl, UNSELECTED);
                                    } else {
                                        Snackbar.make(mCtbLayout, "网络异常", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

}

