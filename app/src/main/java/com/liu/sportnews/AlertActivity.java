package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.bean.Alert2MainEvent;
import com.liu.sportnews.bean.CommentBean;
import com.liu.sportnews.bean.MyCommentBean;
import com.liu.sportnews.bean.Service2MainEvent;
import com.liu.sportnews.bean.TimeLineBean;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DateFormatUtils;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class AlertActivity extends AppCompatActivity {

    @BindView(R.id.alert_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        ButterKnife.bind(this);
        Logger.d("AlertActivity");

        mToolbar.setTitle("回复我的");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String flag = getIntent().getStringExtra("from");
        if (flag.equals("Main")) {
            int nowCommCount = getIntent().getIntExtra("nowCommCount", 0);
            insertCommCount(nowCommCount);
            List<MyCommentBean.Comments> commentsList = (List<MyCommentBean.Comments>) getIntent().getSerializableExtra("commList");
            if(commentsList.size() == 0){
                Toast.makeText(AlertActivity.this, "当前没有未处理的消息", Toast.LENGTH_SHORT).show();
            }
            initData(commentsList);
        } else {
            getCommentFromServer();
        }
    }

    private void insertCommCount(int nowCommCount) {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_ADD_COMMENT_COUNT)
                .addParams(Config.USERNAME, Config.login_name)
                .addParams("commCount", nowCommCount + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            int status = jObject.getInt(Config.STATUS);
                            if (status == 1) {
                                EventBus.getDefault().post(new Alert2MainEvent(new ArrayList<MyCommentBean.Comments>()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getCommentFromServer() {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_GET_USER_COMMENT)
                .addParams(Config.USERNAME, Config.login_name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        MyCommentBean bean = gson.fromJson(response, MyCommentBean.class);
                        int status = bean.status;
                        if(status == 1){
                            List<MyCommentBean.Comments> commList = bean.comments;
                            initData(commList);
                        }else{
                            Toast.makeText(AlertActivity.this, "暂时没有人回复您的帖子", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void initData(final List<MyCommentBean.Comments> commentsList) {
        AlertAdapter adapter = new AlertAdapter(AlertActivity.this, commentsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AlertActivity.this));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Intent intent = new Intent(AlertActivity.this, CommentActivity.class);
                TimeLineBean.TimelineInfo timelineInfo = commentsList.get(i).timeline;
                intent.putExtra("timelineId", Integer.toString(timelineInfo.timelineId));
                intent.putExtra("headInfo", commentsList.get(i).timeline);
                startActivity(intent);
            }
        });
    }

    public class AlertAdapter extends BaseQuickAdapter<MyCommentBean.Comments> {

        private Context context;

        public AlertAdapter(Context context, List<MyCommentBean.Comments> data) {
            super(R.layout.second_comm_layout, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder holder, MyCommentBean.Comments comments) {
            holder.setOnClickListener(R.id.cm_item, new OnItemChildClickListener());
            holder.setText(R.id.cm_nickname, comments.nickname);
            holder.setText(R.id.cm_comment, comments.comment);
            String myNickName = SharedPrerensUtils.getString(context, Config.NICKNAME_KEY);
            holder.setText(R.id.cm_second_nick, "@" + myNickName + ":");
            holder.setText(R.id.cm_second_content, comments.timeline.content);
            //设置日期
            String date = DateFormatUtils.getTimesToNow(comments.date);
            holder.setText(R.id.cm_date, date);
            //设置性别
            int imageId = 0;
            if (comments.sex.equals("女")) {
                imageId = R.drawable.female;
            } else if (comments.sex.equals("男")) {
                imageId = R.drawable.male;
            }
            if (imageId != 0) {
                holder.setImageResource(R.id.cm_sex_icon, imageId);
            }

            String headUrl = comments.headUrl;
            if (!TextUtils.isEmpty(headUrl)) {
                Glide.with(context)
                        .load(comments.headUrl)
                        .centerCrop()
                        .crossFade()
                        .into((ImageView) holder.getView(R.id.cm_head_image));
            }
        }
    }
}
