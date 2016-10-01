package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.bean.WeChatBean;
import com.liu.sportnews.bean.WeChatBean.WeChatList;
import com.liu.sportnews.utils.CacheUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WeChatActivity extends AppCompatActivity implements BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.pic_recycler)
    RecyclerView mRecycler;

    @BindView(R.id.pic_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.wechat_swipe)
    SwipeRefreshLayout mRefreshLayout;

    private WeChatAdapter adapter;

    int mCurrentPage = 1;//当前的页数
    private static final int PAGE_SIZE = 20;//每一页新闻数量
    private String mUrl = Config.WE_CHAT_URL + "pno=" + mCurrentPage + "&key=" + Config.WE_CHAT_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_toolbar);
        ButterKnife.bind(this);

        mToolbar.setTitle("精选文章");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String cache = CacheUtils.getCache(getApplicationContext(), mUrl);
        if (!TextUtils.isEmpty(cache)) {
            initAdapter(cache);
        }
        getDataFromServer(mUrl);

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.colorYellow);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer(mUrl);
            }
        });
    }

    public void getDataFromServer(final String url) {
        if (url != null) {
            OkHttpUtils.get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(WeChatActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("response", response);
                            if (!TextUtils.isEmpty(response)) {
                                initAdapter(response);
                                CacheUtils.setCache(getApplicationContext(), url, response);
                            }
                        }
                    });
        }
    }

    public List<WeChatList> parseData(String result) {
        List<WeChatList> list = new ArrayList<>();
        Gson gson = new Gson();
        WeChatBean weChatBean = gson.fromJson(result, WeChatBean.class);
        list = weChatBean.result.list;
        return list;
    }


    private void initAdapter(String result) {
        mRefreshLayout.setRefreshing(false);
        List<WeChatList> mNewsList = parseData(result);

        adapter = new WeChatAdapter(this, mNewsList);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(adapter);

        adapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter helper, View view, int i) {
                Intent intent = new Intent(WeChatActivity.this, NewsDetailActivity.class);
                WeChatList weChatList = (WeChatList) helper.getItem(i);
                intent.putExtra(Config.WECHAT_DATA, weChatList);
                startActivity(intent);
            }
        });

        /*
        下拉加载
         */
        adapter.setOnLoadMoreListener(this);
        adapter.openLoadMore(PAGE_SIZE, true);
        adapter.openLoadAnimation();
    }

    /*
    下拉加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        mRecycler.post(new Runnable() {
            @Override
            public void run() {
                ++mCurrentPage;
                String url = Config.WE_CHAT_URL + "pno=" + mCurrentPage + "&key=" + Config.WE_CHAT_KEY;
                OkHttpUtils.get()
                        .url(url)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(WeChatActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("response", response);
                                if (!TextUtils.isEmpty(response)) {
                                    adapter.notifyDataChangedAfterLoadMore(parseData(response), true);
                                }
                            }
                        });
            }

        });
    }

    public class WeChatAdapter extends BaseQuickAdapter<WeChatList> {

        private Context mContext;

        public WeChatAdapter(Context context, List<WeChatList> data) {
            super(R.layout.we_chat_item, data);
            mContext = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, WeChatList newsList) {
            helper.setText(R.id.title, newsList.title);
            helper.setText(R.id.author, newsList.source);
            Glide.with(mContext)
                    .load(newsList.firstImg)
                    .centerCrop()
                    .placeholder(R.drawable.login)
                    .into((ImageView) helper.getView(R.id.wechat_pic));

            helper.setOnClickListener(R.id.enter, new OnItemChildClickListener());
        }
    }
}
