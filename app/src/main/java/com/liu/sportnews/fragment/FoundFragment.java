package com.liu.sportnews.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.MyCenterActivity;
import com.liu.sportnews.PicActivity;
import com.liu.sportnews.R;
import com.liu.sportnews.RobotActivity;
import com.liu.sportnews.TimeLineActivity;
import com.liu.sportnews.WeChatActivity;
import com.liu.sportnews.WeatherActivity;
import com.liu.sportnews.bean.FoundBean;
import com.liu.sportnews.bean.InfoBean;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DividerItemDecoration;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by Liujianfeng on 2016/7/9.
 */
public class FoundFragment extends Fragment {

    private static Context mContext;
    private ArrayList<FoundBean> mFoundList = new ArrayList<>();

    private String[] mTitleArray = {"ok机器人", "天气预报", "NBA赛事", "足球赛事"};
    private int[] mIconArray = {R.drawable.robot, R.drawable.weather, R.drawable.basketball,
            R.drawable.football3};

    private RecyclerView mRecyclerView;
    private CircleImageView mHeaderImage;
    private FoundAdapter mAdapter;
    private View mHeadView;
    private TextView mNickname;
    private TextView mDateText;
    private RelativeLayout mTimeline;
    private RelativeLayout mPicture;
    private RelativeLayout mWechat;

    public static FoundFragment newInstance(Context context) {
        FoundFragment roomFragment = new FoundFragment();
        mContext = context;
        return roomFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.room_layout, container, false);
        if (mFoundList.size() == 0) {
            initArrayList();
        }
        Logger.d("FoundFragment");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.found_recycler);
        mHeaderImage = (CircleImageView) v.findViewById(R.id.header);
        mHeadView = v.findViewById(R.id.found_head);
        mNickname = (TextView) v.findViewById(R.id.name);//用户昵称
        mDateText = (TextView) v.findViewById(R.id.reg_date);//注册时长
        mTimeline = (RelativeLayout) v.findViewById(R.id.found_timeline);
        mPicture = (RelativeLayout) v.findViewById(R.id.found_picture);
        mWechat = (RelativeLayout) v.findViewById(R.id.found_wechat);

        initView();
        setListener();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SharedPrerensUtils.getBoolean(mContext, Config.ISLOGIN)){
            String imageUrl = SharedPrerensUtils.getString(mContext, Config.HEAD_URL_KEY);
            String nickname = SharedPrerensUtils.getString(mContext, Config.NICKNAME_KEY);
            parseData(imageUrl, nickname);
            getInfoFromServer();
        }
    }

    public void getInfoFromServer() {
        if (SharedPrerensUtils.getBoolean(mContext, Config.ISLOGIN)) {
            OkHttpUtils.post()
                    .url(Config.LOCAL_URL)
                    .addParams(Config.ACTION, Config.ACTION_SEARCH_INFO)
                    .addParams(Config.USERNAME, Config.login_name)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("showHeadImageFromServer", response);
                            if (response != null) {
                                Gson gson = new Gson();
                                InfoBean mInfoBean = gson.fromJson(response, InfoBean.class);
                                String imageUrl = mInfoBean.headUrl;
                                String nickname = mInfoBean.nickname;
                                if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(nickname)) {
                                    parseData(imageUrl, nickname);
                                }
                            }
                        }
                    });
        }
    }

    public void parseData(String imageUrl, String nickname) {
        if (!TextUtils.isEmpty(nickname)) {
            mNickname.setText(nickname);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            Log.d("FoundImage", imageUrl);
            Glide.with(mContext)
                    .load(imageUrl)
                    .centerCrop()
                    .crossFade()
                    .into(mHeaderImage);
        }
        String days = SharedPrerensUtils.getString(mContext, Config.REGISTE_DATE);
        if(!TextUtils.isEmpty(days)){
            mDateText.setText("加入社区" + days + "天");
        }
    }

    private void initView() {
        mAdapter = new FoundAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.login_status) {
                    startActivity(new Intent(getActivity(), MyCenterActivity.class));
                } else {
                    Toast.makeText(mContext, "请登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.login_status) {
                    startActivity(new Intent(mContext, TimeLineActivity.class));
                    //getActivity().finish();
                } else {
                    Toast.makeText(mContext, "请登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, PicActivity.class));
            }
        });

        mWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, WeChatActivity.class));
            }
        });

        //响应列表监听
        mAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter helper, View view, int i) {
                switch (view.getId()) {
                    case R.id.found_item:
                        switch (i) {
                            case 0:
                                startActivity(new Intent(mContext, RobotActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(mContext, WeatherActivity.class));
                                break;
                            case 2:
                                Toast.makeText(mContext, "准备上线，敬请期待", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(mContext, "准备上线，敬请期待", Toast.LENGTH_SHORT).show();
                                break;
                        }
                }
            }
        });

    }

    private void initArrayList() {
        for (int i = 0; i < mTitleArray.length; i++) {
            FoundBean bean = new FoundBean();
            bean.title = mTitleArray[i];
            bean.drawable = mIconArray[i];
            mFoundList.add(bean);
        }
    }

    public class FoundAdapter extends BaseQuickAdapter<FoundBean> {

        public FoundAdapter() {
            super(R.layout.found_item_layout, mFoundList);
        }

        @Override
        protected void convert(BaseViewHolder helper, FoundBean foundBean) {
            helper.setText(R.id.title, foundBean.title);
            helper.setImageResource(R.id.icon, foundBean.drawable);

            //为头布局和子布局添加监听事件
            helper.setOnClickListener(R.id.found_item, new OnItemChildClickListener());
        }
    }


}
