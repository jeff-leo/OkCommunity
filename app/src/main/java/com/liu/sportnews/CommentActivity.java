package com.liu.sportnews;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liu.sportnews.bean.CommentBean;
import com.liu.sportnews.bean.CommentBean.Comments;
import com.liu.sportnews.bean.TimeLineBean.TimelineInfo;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DateFormatUtils;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class CommentActivity extends AppCompatActivity {

    @BindView(R.id.comment_list)
    ListView mListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.cm_edit)
    EditText mEdit;
    @BindView(R.id.cm_send)
    Button mBtnSend;
    @BindView(R.id.cm_pb)
    ProgressBar mProgressBar;

    private TimelineInfo mHeadInfo;
    private String mTimelineId;
    private CommentAdapter mAdapter;
    private List<Comments> mComments = new ArrayList<>();
    private boolean first = true;//第一次进入和之后的刷新分不同操作

    private TextView cmCount;
    private TextView commentCount;

    private String mCurrentCommPos = "";//当前回复的帖子的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        Logger.init("CommentActivity");

        mToolbar.setTitle("评论详情");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTimelineId = getIntent().getStringExtra("timelineId");
        mHeadInfo = (TimelineInfo) getIntent().getSerializableExtra("headInfo");
        mProgressBar.setVisibility(View.VISIBLE);
        if (mTimelineId != null) {
            getDataFromServer(mTimelineId);
        }
        setListener();
    }

    private void setListener() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEdit.getText().toString();
                String currentNickname = SharedPrerensUtils.getString(CommentActivity.this, Config.NICKNAME_KEY);
                String myHeadUrl = SharedPrerensUtils.getString(CommentActivity.this, Config.HEAD_URL_KEY);
                String mySex = SharedPrerensUtils.getString(CommentActivity.this, Config.SEX_KEY);

                if(!TextUtils.isEmpty(currentNickname) && !TextUtils.isEmpty(mySex) && !TextUtils.isEmpty(myHeadUrl)){
                    OkHttpUtils.post()
                            .url(Config.LOCAL_URL)
                            .addParams(Config.ACTION, Config.ACTION_ADD_COMMENT)
                            .addParams("nickname", currentNickname)
                            .addParams("sex", mySex)
                            .addParams("comment", text)
                            .addParams("headUrl", myHeadUrl)
                            .addParams("timelineId", mHeadInfo.timelineId + "")
                            .addParams("tlCommPos", mComments.size()+"")
                            .addParams("secondCommId", mCurrentCommPos)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(CommentActivity.this, "网络异常, 评论失败", Toast.LENGTH_SHORT).show();
                                    mCurrentCommPos = "";//重置回复id
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    mCurrentCommPos = "";//重置回复id
                                    try {
                                        JSONObject jObject = new JSONObject(response);
                                        int status = jObject.getInt(Config.STATUS);
                                        Logger.d(status);
                                        if (status == 1) {
                                            getDataFromServer(mTimelineId);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                }else{
                    Toast.makeText(CommentActivity.this, "为保证社区质量，请完善个人信息", Toast.LENGTH_SHORT).show();
                }
                mEdit.setText("");

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String[] list = {"复制评论", "回复评论"};
                final int currentPos = position - 2;
                final String clipText = mComments.get(currentPos).comment;
                final String editText = "@"+mComments.get(currentPos).nickname + ":";
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                        builder.setItems(list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ClipboardManager manager = (ClipboardManager) CommentActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                        manager.setText(clipText);
                                        break;
                                    case 1:
                                        mEdit.setText(editText);
                                        mEdit.setSelection(editText.length());
                                        mCurrentCommPos = Integer.toString(currentPos);
                                        Logger.d("mListView" + mCurrentCommPos);
                                        break;
                                }
                            }
                        }).show();
                    }
                });
            }
        });
    }

    private void getDataFromServer(String timelineId) {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_GET_COMMENT)
                .addParams("timelineId", timelineId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(CommentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mProgressBar.setVisibility(View.GONE);
                        Logger.d(response);
                        if (response != null) {
                            Gson gson = new Gson();
                            CommentBean bean = gson.fromJson(response, CommentBean.class);
                            if (first) {
                                mComments.addAll(bean.comments);
                                mAdapter = new CommentAdapter(CommentActivity.this, mComments);
                                mListView.setAdapter(mAdapter);
                                first = false;
                                /*
                                第一次进入评论详情时才加载头布局
                                 */
                                View firstHead = View.inflate(CommentActivity.this, R.layout.timeline_item_layout, null);
                                initFirstHeadData(firstHead);
                                View secondHead = View.inflate(CommentActivity.this, R.layout.comment_head_layout, null);
                                initSecondHeadData(secondHead);
                                mListView.addHeaderView(firstHead);
                                mListView.addHeaderView(secondHead);
                            } else {
                                mComments.clear();
                                mComments.addAll(bean.comments);
                                mAdapter.notifyDataSetChanged();
                                cmCount.setText(mComments.size() + "");
                                commentCount.setText("最新评论 " + mComments.size());
                                mListView.setSelection(mComments.size() - 1);
                            }
                        }
                    }
                });
    }

    private void initFirstHeadData(View headView) {
        TextView nickText = (TextView) headView.findViewById(R.id.tl_nickname);
        TextView cityText = (TextView) headView.findViewById(R.id.tl_area);
        TextView date = (TextView) headView.findViewById(R.id.tl_date);
        TextView content = (TextView) headView.findViewById(R.id.tl_content);
        cmCount = (TextView) headView.findViewById(R.id.tl_comment_count);
        ImageView sexIcon = (ImageView) headView.findViewById(R.id.tl_sex_icon);
        CircleImageView headImage = (CircleImageView) headView.findViewById(R.id.tl_head_image);

        if(mHeadInfo != null){
            nickText.setText(mHeadInfo.nickname);
            cityText.setText(mHeadInfo.city);
            cmCount.setText(mComments.size() + "");
            String times = DateFormatUtils.getTimesToNow(mHeadInfo.date);
            date.setText(times);
            content.setText(mHeadInfo.content);
            if (mHeadInfo.sex.equals("女")) {
                sexIcon.setImageResource(R.drawable.female);
            } else {
                sexIcon.setImageResource(R.drawable.male);
            }

            if (!TextUtils.isEmpty(mHeadInfo.headUrl)) {
                Glide.with(this)
                        .load(mHeadInfo.headUrl)
                        .centerCrop()
                        .crossFade()
                        .into(headImage);
            }
        }
    }

    private void initSecondHeadData(View secondHead) {
        commentCount = (TextView) secondHead.findViewById(R.id.count);
        commentCount.setText("热门评论");
    }

    /*
    初始化评论布局
     */
    public class CommentAdapter extends BaseAdapter {

        private List<Comments> commentList;
        private LayoutInflater inflater;
        private Context context;

        public CommentAdapter(Context context, List<Comments> commentList) {
            this.commentList = commentList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            String secondId = commentList.get(position).secondCommId;
            if(secondId.equals("")){
                return 0;//无二级评论
            }else{
                return 1;//有二级评论
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comments comments = commentList.get(position);
            View view = null;
            ViewHolder holer;
            if (convertView == null) {
                if(getItemViewType(position) == 1){//有二级评论的item
                    view = inflater.inflate(R.layout.second_comm_layout, parent, false);
                    holer = new ViewHolder();
                    holer.secondNick = (TextView) view.findViewById(R.id.cm_second_nick);
                    holer.secondComment = (TextView) view.findViewById(R.id.cm_second_content);
                }else{//无二级评论的item
                    view = inflater.inflate(R.layout.comment_item_layout, parent, false);
                    holer = new ViewHolder();
                }
                holer.headImage = (CircleImageView) view.findViewById(R.id.cm_head_image);
                holer.nickText = (TextView) view.findViewById(R.id.cm_nickname);
                holer.sexIcon = (ImageView) view.findViewById(R.id.cm_sex_icon);
                holer.contentText = (TextView) view.findViewById(R.id.cm_comment);
                holer.dateText = (TextView) view.findViewById(R.id.cm_date);
                holer.floor = (TextView) view.findViewById(R.id.cm_floor);
                view.setTag(holer);
            } else {
                view = convertView;
                holer = (ViewHolder) view.getTag();
            }

            //设置二级昵称评论
            if(getItemViewType(position) == 1){
                int secondPos = Integer.valueOf(comments.secondCommId);
                String secondNick = commentList.get(secondPos).nickname;
                String secondComm = commentList.get(secondPos).comment;
                holer.secondNick.setText("@" + secondNick + ":");
                holer.secondComment.setText(secondComm);
            }

            //设置昵称
            String nickname = comments.nickname;
            if(nickname.equals(mHeadInfo.nickname)){
                holer.nickText.setText(nickname + "(楼主)");
            }else{
                holer.nickText.setText(nickname);
            }
            //设置楼数
            holer.floor.setText(position + 1 + "楼");
            //设置评论信息
            holer.contentText.setText(comments.comment);
            //设置日期
            String date = comments.date;
            String times = DateFormatUtils.getTimesToNow(date);
            holer.dateText.setText(times);
            //设置性别
            if (comments.sex.equals("女")) {
                holer.sexIcon.setImageResource(R.drawable.female);
            } else if(comments.sex.equals("男")){
                holer.sexIcon.setImageResource(R.drawable.male);
            } else{
                holer.sexIcon.setVisibility(View.GONE);
            }
            //设置头像
            String headUrl = comments.headUrl;
            if (!TextUtils.isEmpty(headUrl)) {
                Glide.with(context)
                        .load(headUrl)
                        .centerCrop()
                        .crossFade()
                        .into(holer.headImage);
            }
            return view;
        }

        class ViewHolder {
            CircleImageView headImage;
            TextView nickText;
            ImageView sexIcon;
            TextView contentText;
            TextView dateText;
            TextView floor;
            TextView secondNick;
            TextView secondComment;
        }
    }
}
