package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.bean.CommentBean;
import com.liu.sportnews.bean.TimeLineBean;
import com.liu.sportnews.bean.TimeLineEvent;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DateFormatUtils;
import com.liu.sportnews.utils.DividerItemDecoration;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MyTimeLineActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.my_tl_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.my_tl_pb)
    ProgressBar mPb;

    private List<TimeLineBean.TimelineInfo> mTimelineList;
    private int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_time_line);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Logger.d("MyTimeline");

        mToolbar.setTitle("我的帖子");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPb.setVisibility(View.VISIBLE);
        getTimelineInfoFromServer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timelineEvent(TimeLineEvent event){
        if(event.flag == 1){
            getTimelineInfoFromServer();
        }
    }

    private void getTimelineInfoFromServer() {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_GET_TIMELINE)
                .addParams(Config.USERNAME, Config.login_name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mPb.setVisibility(View.GONE);
                        Toast.makeText(MyTimeLineActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mPb.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(response)) {
                            parseData(response);
                        }
                    }
                });
    }

    private void parseData(String response) {
        Gson gson = new Gson();
        TimeLineBean timeLineBean = gson.fromJson(response, TimeLineBean.class);
        int status = timeLineBean.status;
        if (status == 1) {
            mTimelineList = timeLineBean.timeline;
            TimelineAdapter mAdapter = new TimelineAdapter(mTimelineList);
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(mAdapter);
            mRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    mCurrentPosition = i;
                    Intent intent = new Intent(MyTimeLineActivity.this, CommentActivity.class);
                    intent.putExtra("timelineId", Integer.toString(mTimelineList.get(i).timelineId));
                    intent.putExtra("headInfo", mTimelineList.get(i));
                    startActivity(intent);
                    //finish();
                }
            });
            mRecycler.smoothScrollToPosition(mCurrentPosition);
        } else {
            Toast.makeText(MyTimeLineActivity.this, "你还未发过帖子", Toast.LENGTH_SHORT).show();
        }
    }

    public class TimelineAdapter extends BaseQuickAdapter<TimeLineBean.TimelineInfo> {

        public TimelineAdapter(List<TimeLineBean.TimelineInfo> data) {
            super(R.layout.timeline_item_layout, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, TimeLineBean.TimelineInfo timeLineBean) {
            holder.setOnClickListener(R.id.tl_item_layout, new OnItemChildClickListener());
            holder.setText(R.id.tl_nickname, timeLineBean.nickname);
            holder.setText(R.id.tl_area, timeLineBean.city);
            holder.setText(R.id.tl_content, timeLineBean.content);
            holder.setText(R.id.tl_comment_count, timeLineBean.cmCount + "");
            String times = DateFormatUtils.getTimesToNow(timeLineBean.date);
            holder.setText(R.id.tl_date, times);
            if (timeLineBean.sex.equals("女")) {
                holder.setImageResource(R.id.tl_sex_icon, R.drawable.female);
            } else {
                holder.setImageResource(R.id.tl_sex_icon, R.drawable.male);
            }
            Glide.with(MyTimeLineActivity.this)
                    .load(timeLineBean.headUrl)
                    .centerCrop()
                    .crossFade()
                    .into((ImageView) holder.getView(R.id.tl_head_image));
        }
    }
}
