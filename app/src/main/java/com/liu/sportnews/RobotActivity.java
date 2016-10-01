package com.liu.sportnews;

import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.liu.sportnews.bean.ChatMsgBean;
import com.liu.sportnews.bean.InfoBean;
import com.liu.sportnews.bean.ResultMsgBean;
import com.liu.sportnews.utils.DatabaseHelper;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DateFormatUtils;
import com.liu.sportnews.utils.InfoDBServerUtils;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.liu.sportnews.utils.ShowHeaderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class RobotActivity extends AppCompatActivity {

    @BindView(R.id.pic_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pic_recycler)
    RecyclerView mRecycler;

    @BindView(R.id.btn_send)
    Button mBtnSend;

    @BindView(R.id.text_send)
    EditText mTextSend;

    private List<ChatMsgBean> mChatMessage = new ArrayList<>();
    private RobotAdapter mAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private DatabaseHelper mHelper;
    private String mImageUrl;
    private String mNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        ButterKnife.bind(this);
        mToolbar.setTitle("智能机器人");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        创建数据库
         */
        mHelper = InfoDBServerUtils.helper;

        mImageUrl = SharedPrerensUtils.getString(RobotActivity.this, Config.HEAD_URL_KEY);
        mNickname = SharedPrerensUtils.getString(RobotActivity.this, Config.NICKNAME_KEY);
        getInfoFromServer();
        setListener();
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
                            Toast.makeText(RobotActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("showHeadImageFromServer", response);
                            if (response != null) {
                                Gson gson = new Gson();
                                InfoBean mInfoBean = gson.fromJson(response, InfoBean.class);
                                mImageUrl = mInfoBean.headUrl;
                                mNickname = mInfoBean.nickname;
                                initData();
                            }
                        }
                    });
        }
    }

    private void setListener() {
        mTextSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mRecycler.smoothScrollToPosition(mChatMessage.size() - 1);
                        break;
                }
                return false;
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMsgBean bean = new ChatMsgBean();
                String sendText = mTextSend.getText().toString();

                if (!TextUtils.isEmpty(sendText)) {
                    bean.info = sendText;
                    String date = dateFormat.format(new Date());
                    bean.date = date;
                    bean.setItemType(ChatMsgBean.SENDING);
                    //存数据库
                    initDataBase(sendText, date, ChatMsgBean.SENDING);

                    mChatMessage.add(bean);
                    mAdapter.notifyDataSetChanged();

                    mTextSend.setText("");
                    getDataFromServer(sendText);
                } else {
                    Toast.makeText(RobotActivity.this, "未输入信息", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initDataBase(String content, String date, int type) {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", Config.login_name);
        values.put("type", type);
        values.put("date", date);
        values.put("content", content);
        database.insert("ChatMessage", null, values);
    }

    public void getDataFromServer(String sendInfo) {
        String url = Config.TULING_URL;
        String appKey = Config.ROBOT_KEY;
        OkHttpUtils.post()
                .url(url)
                .addParams("key", appKey)
                .addParams("info", sendInfo)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(RobotActivity.this, "消息返回失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                ResultMsgBean result = gson.fromJson(response, ResultMsgBean.class);
                ChatMsgBean bean = new ChatMsgBean();
                bean.setItemType(ChatMsgBean.COMING);
                bean.info = result.text;
                String date = dateFormat.format(new Date());
                bean.date = date;
                //存数据库
                initDataBase(result.text, date, ChatMsgBean.COMING);

                mChatMessage.add(bean);
                mAdapter.notifyDataSetChanged();
                mRecycler.smoothScrollToPosition(mChatMessage.size() - 1);
            }
        });
    }

    private void initData() {
        ChatMsgBean initBean = new ChatMsgBean();
        initBean.info = "你好，很高兴你找我聊天";
        initBean.date = dateFormat.format(new Date());
        initBean.setItemType(ChatMsgBean.COMING);
        mChatMessage.add(initBean);

        //取数据库
        List<ChatMsgBean> dataList = getDataBase();
        if(dataList.size() != 0){
            mChatMessage.addAll(dataList);
        }
        mAdapter = new RobotAdapter(mChatMessage);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(manager);
        mRecycler.smoothScrollToPosition(mChatMessage.size() - 1);
        mRecycler.setAdapter(mAdapter);
    }

    private List<ChatMsgBean> getDataBase() {
        List<ChatMsgBean> list = new ArrayList<>();
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = database.query("ChatMessage", null, "username = ?", new String[]{Config.login_name}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                ChatMsgBean msgBean = new ChatMsgBean();
                msgBean.setItemType(type);
                msgBean.date = date;
                msgBean.info = content;
                list.add(msgBean);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void showDialog(final TextView view){
        final String[] list = {"复制", "撤回", "删除"};
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RobotActivity.this);
                builder.setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                ClipboardManager manager = (ClipboardManager) RobotActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                manager.setText(view.getText());
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    public class RobotAdapter extends BaseMultiItemQuickAdapter<ChatMsgBean> {
        public RobotAdapter(List<ChatMsgBean> data) {
            super(data);
            addItemType(ChatMsgBean.SENDING, R.layout.item_to_msg);
            addItemType(ChatMsgBean.COMING, R.layout.item_from_msg);
        }

        @Override
        protected void convert(BaseViewHolder helper, ChatMsgBean chatMsgItem) {
            switch (helper.getItemViewType()){
                case ChatMsgBean.SENDING:
                    helper.setText(R.id.tv_to_msg_date, DateFormatUtils.getTimesToNow(chatMsgItem.date));
                    helper.setText(R.id.tv_to_msg, chatMsgItem.info);
                    helper.setText(R.id.tv_username, "我");
                    if(!TextUtils.isEmpty(mImageUrl)){
                        Glide.with(RobotActivity.this)
                                .load(mImageUrl)
                                .centerCrop()
                                .crossFade()
                                .into((ImageView) helper.getView(R.id.rb_head_image));
                    }
                    showDialog((TextView) helper.getView(R.id.tv_to_msg));
                    break;
                case ChatMsgBean.COMING:
                    helper.setText(R.id.tv_from_msg_date, DateFormatUtils.getTimesToNow(chatMsgItem.date));
                    helper.setText(R.id.tv_from_msg, chatMsgItem.info);
                    showDialog((TextView) helper.getView(R.id.tv_from_msg));
                    break;
            }
        }
    }
}
