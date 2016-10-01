package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liu.sportnews.utils.CheckVersion;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.SharedPrerensUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.setting_list)
    ListView mListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    String[] mTitle = {"检查更新", "清除缓存(app出现问题请尝试清除缓存)", "关于开发者", "用户反馈"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView.setAdapter(new SettingAdapter(this, mTitle));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        CheckVersion.checkVersion(SettingActivity.this, Config.ITEM);
                        break;
                    case 1:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(SettingActivity.this).clearDiskCache();
                            }
                        }).start();
                        Glide.get(SettingActivity.this).clearMemory();
                        Toast.makeText(SettingActivity.this, "已成功清除缓存", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        startActivity(new Intent(SettingActivity.this, DevelopInfoActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(SettingActivity.this, SendFeedBackActivity.class));
                        break;
                }
            }
        });
    }

    public class SettingAdapter extends BaseAdapter{

        private Context context;
        private String[] title;
        private LayoutInflater inflater;

        public SettingAdapter(Context context, String[] title){
            this.context = context;
            this.title = title;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitle[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.center_item, parent, false);
                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.left_text);
                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            holder.textView.setText(title[position]);
            holder.textView.setTextColor(Color.parseColor("#000000"));
            return convertView;
        }

        class Holder{
            TextView textView;
        }
    }
}
