package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liu.sportnews.bean.NewsBean;
import com.liu.sportnews.bean.NewsBean.NewsDetailList;
import com.liu.sportnews.utils.CacheUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.ConnectionUtils;
import com.liu.sportnews.utils.StatusUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.ca_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.ca_collapsing_bar)
    CollapsingToolbarLayout mCtbLayout;

    @BindView(R.id.ca_image)
    ImageView mImage;

    @BindView(R.id.ca_recycleView)
    RecyclerView mRecyclerView;

    @BindView(R.id.ca_swipeRefresh)
    SwipeRefreshLayout mRefreshLayout;

    public Context mContext = this;
    private String mCategoryUrl;

    private List<NewsDetailList> mCateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        获取CategoryFragment中传来的url
         */
        Bundle bundle = getIntent().getExtras().getBundle("data");
        mCategoryUrl = bundle.getString("categoryUrl");
        String imageUrl = bundle.getString("imageUrl");
        String categoryText = bundle.getString("categoryText");

        mCtbLayout.setTitle(categoryText);
        mCtbLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCtbLayout.setExpandedTitleColor(Color.WHITE);
        mCtbLayout.setExpandedTitleMarginStart(40);
        mCtbLayout.setExpandedTitleMarginBottom(30);

        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .crossFade()
                .into(mImage);

        String cache = CacheUtils.getCache(mContext, mCategoryUrl);
        if (!TextUtils.isEmpty(cache)) {
            parseData(cache);
        }
        getDataFromServer();
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
                getDataFromServer();
            }
        });
    }

    private void getDataFromServer() {
        OkHttpUtils.get()
                .url(mCategoryUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        EventBus.getDefault().post(Config.FAIL);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CacheUtils.setCache(mContext, mCategoryUrl, response);
                        EventBus.getDefault().post(response);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void parseData(String result) {
        mRefreshLayout.setRefreshing(false);
        if (result.equals(Config.FAIL)) {
            Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            NewsBean newsBean = gson.fromJson(result, NewsBean.class);
            mCateList = newsBean.result.data;

            CategoryDetailAdp adapter = new CategoryDetailAdp();
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayout.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public class CategoryDetailAdp extends RecyclerView.Adapter<CategoryDetailAdp.DetailHolder> {

        @Override
        public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, parent, false);
            return new DetailHolder(view);
        }

        @Override
        public void onBindViewHolder(DetailHolder holder, final int position) {
            if (mCateList != null) {
                holder.contentText.setText(mCateList.get(position).title);
                holder.author.setText(mCateList.get(position).author_name);
                holder.date.setText(mCateList.get(position).date);

                Glide.with(mContext)
                        .load(mCateList.get(position).thumbnail_pic_s)
                        .centerCrop()
                        .placeholder(R.drawable.defaultpic)
                        .crossFade()
                        .into(holder.imageView);

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //最好设置回调
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra(Config.NEWS_DATA, mCateList.get(position));
                        startActivity(intent);
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return mCateList.size();
        }


        class DetailHolder extends RecyclerView.ViewHolder {
            LinearLayout cardView;
            ImageView imageView;
            TextView contentText;
            TextView author;
            TextView date;

            public DetailHolder(View itemView) {
                super(itemView);
                cardView = (LinearLayout) itemView.findViewById(R.id.cv_id);
                imageView = (ImageView) itemView.findViewById(R.id.cv_image);
                contentText = (TextView) itemView.findViewById(R.id.cv_text);
                author = (TextView) itemView.findViewById(R.id.author);
                date = (TextView) itemView.findViewById(R.id.date);
            }
        }
    }

}
