package com.liu.sportnews.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liu.sportnews.MainActivity;
import com.liu.sportnews.NewsDetailActivity;
import com.liu.sportnews.R;
import com.liu.sportnews.adapter.RVAdapter;
import com.liu.sportnews.bean.NewsBean;
import com.liu.sportnews.bean.NewsBean.NewsDetailList;
import com.liu.sportnews.utils.CacheUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.ConnectionUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Liujianfeng on 2016/7/9.
 */
public class MainNewsFragment extends Fragment {

    //最好使用newinstance方式来获取fragment实例

    private static Context mContext;

    private RVAdapter adapter;

    //总的新闻条数
    private List<NewsDetailList> mHomeList = null;

    private List<NewsDetailList> mTopList = new ArrayList<>();
    private List<NewsDetailList> mItemList = new ArrayList<>();

    public static MainNewsFragment newInstance(Context context) {
        MainNewsFragment newsFragment = new MainNewsFragment();
        mContext = context;
        Logger.d("MainNewsFragment");
        return newsFragment;
    }

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mManager;

    private static final String MAIN_URL = Config.REQUEST_URL + Config.TOP + Config.KEY
            + Config.APP_KEY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("register");
        Logger.init("MainNewsFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_news_layout, container, false);
        //获得RecycleView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycleView);
        mManager = new LinearLayoutManager(mContext);

        //获取RefreshLayout
        mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String cache = CacheUtils.getCache(mContext, MAIN_URL);

        if (!TextUtils.isEmpty(cache)) {
            parseData(cache);
        }
        getDataFromServer();
        //RefreshLayout的设置
        mRefreshLayout.setColorSchemeResources(R.color.colorYellow);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });

    }

    //是否传递参数
    private void getDataFromServer() {
        OkHttpUtils.get()
                .url(MAIN_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CacheUtils.setCache(mContext, MAIN_URL, response);
                        if(!TextUtils.isEmpty(response)){
                            parseData(response);
                        }
                    }
                });
    }

    public void parseData(String result) {
        mRefreshLayout.setRefreshing(false);
        if (result.equals(Config.FAIL)) {
            Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            NewsBean newsBean = gson.fromJson(result, NewsBean.class);
            if (newsBean.result != null) {
                mHomeList = newsBean.result.data;
                mTopList = mHomeList.subList(0, 5);
                mItemList = mHomeList.subList(5, mHomeList.size());

                adapter = new RVAdapter(mContext, mHomeList, mTopList, mItemList);
                mManager.setOrientation(LinearLayout.VERTICAL);
                mRecyclerView.setLayoutManager(mManager);
                mRecyclerView.setAdapter(adapter);
            }
        }

        if (adapter != null) {
            adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, NewsDetailList itemData) {
                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                    intent.putExtra(Config.NEWS_DATA, itemData);
                    startActivity(intent);
                }
            });
        }

    }

}
