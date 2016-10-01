package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.bean.InfoBean;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.InfoDBServerUtils;
import com.liu.sportnews.utils.DividerItemDecoration;
import com.liu.sportnews.utils.ShowHeaderUtils;
import com.liu.sportnews.utils.StatusUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MyCenterActivity extends AppCompatActivity {

    @BindView(R.id.center_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.center_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_image)
    CircleImageView mUserImage;
    @BindView(R.id.center_nick)
    TextView mNickName;
    @BindView(R.id.sexIcon)
    ImageView mSexIcon;
    @BindView(R.id.center_area)
    TextView mArea;
    @BindView(R.id.under_write)
    TextView mUnderWrite;
    @BindView(R.id.center_edit)
    RelativeLayout mEditBtn;
    @BindView(R.id.center_pb)
    ProgressBar mProgressBar;

    private String[] titles = {"我的社区发帖", "回复我的帖子", "我的新闻评论"};
    private List<String> mList = new ArrayList<>(Arrays.asList(titles));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_my_center);
        ButterKnife.bind(this);

        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        initRecyclerView();
        setListener();
    }

    private void setListener() {
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCenterActivity.this, MyEditActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> infoDBList = InfoDBServerUtils.getDataFromDatabase();
        if(infoDBList != null && !infoDBList.isEmpty()){
            initData(infoDBList);
        }
        getInfoFromServer();
    }

    public void getInfoFromServer() {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_SEARCH_INFO)
                .addParams(Config.USERNAME, Config.login_name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyCenterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mProgressBar.setVisibility(View.GONE);
                        if (response != null) {
                            Gson gson = new Gson();
                            InfoBean bean = gson.fromJson(response, InfoBean.class);
                            List<String> infoList = new ArrayList<>(Arrays.asList(
                                    Config.login_name, bean.nickname, bean.sex,
                                    bean.city, bean.underwrite, bean.headUrl));
                            initData(infoList);
                            Log.d("infoList", bean.nickname);
                        }
                    }
                });
    }

    /*
    处理账号信息逻辑
     */
    private void initData(List<String> list) {
        if(!TextUtils.isEmpty(list.get(1))){
            mNickName.setText(list.get(1));
        }
        if(list.get(2).equals("女")){
            mSexIcon.setImageResource(R.drawable.female);
        }else {
            mSexIcon.setImageResource(R.drawable.male);
        }
        if(!TextUtils.isEmpty(list.get(3))){
            mArea.setText(list.get(3));
        }
        if(!TextUtils.isEmpty(list.get(4))){
            mUnderWrite.setText("个性签名:" + list.get(4));
        }
        if(!TextUtils.isEmpty(list.get(5))){
            Glide.with(this)
                    .load(list.get(5))
                    .centerCrop()
                    .crossFade()
                    .into(mUserImage);
        }
    }

    public void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

        CenterAdapter adapter = new CenterAdapter(mList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (i){
                    case 0:
                        startActivity(new Intent(MyCenterActivity.this, MyTimeLineActivity.class));
                        break;
                    case 1:
                        Intent intent = new Intent(MyCenterActivity.this, AlertActivity.class);
                        intent.putExtra("from", "MyCenter");
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    //RecyclerView的适配器
    public class CenterAdapter extends BaseQuickAdapter<String>{

        public CenterAdapter(List<String> data) {
            super(R.layout.center_item, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.left_text, s);
            baseViewHolder.setOnClickListener(R.id.center_item_layout, new OnItemChildClickListener());
        }
    }
}

