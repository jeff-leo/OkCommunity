package com.liu.sportnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liu.sportnews.Service.WeatherService;
import com.liu.sportnews.bean.ProvinceBean;
import com.liu.sportnews.bean.WeatherBean;
import com.liu.sportnews.bean.WeatherBean.Data;
import com.liu.sportnews.utils.CacheUtils;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.StatusUtil;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WeatherActivity extends AppCompatActivity {

    private String initCity = "广州";
    private String mUrl = Config.WEATHER_URL + initCity + Config.WEATHER_KEY;

    @BindView(R.id.city_date)
    TextView mTextDate;
    @BindView(R.id.city)
    TextView mTextCity;
    @BindView(R.id.city_title)
    TextView mTextTitle;
    @BindView(R.id.city_detail_title)
    TextView mTextChuanyi;
    @BindView(R.id.city_deg)
    TextView mTextDeg;
    @BindView(R.id.city_wind)
    TextView mTextWind;
    @BindView(R.id.city_wind_power)
    TextView mTextWindPower;

    @BindView(R.id.day1)
    TextView mTextDay1;
    @BindView(R.id.day2)
    TextView mTextDay2;
    @BindView(R.id.day3)
    TextView mTextDay3;
    @BindView(R.id.day4)
    TextView mTextDay4;
    @BindView(R.id.day5)
    TextView mTextDay5;
    @BindView(R.id.day6)
    TextView mTextDay6;

    @BindView(R.id.city_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.city_background)
    ImageView mImage;
    @BindView(R.id.add_city)
    RelativeLayout mAddCity;
    @BindView(R.id.refresh)
    RelativeLayout mRefresh;

    private static final String ZHOU = "周";

    private Context mContext;
    private PopupWindow mPopWindow;

    /*
    全部天气信息
     */
    private Data mData;

    private ListView mProvinceList;
    private ListView mCitiesList;

    private List<String> provinceName;//省名
    private List<String> citiesName;//市名

    private String[] mMunicipalities = {"北京市", "天津市", "上海市", "重庆市"};//直辖市直接刷新数据

    private static final int FIRST = 0;
    private static final int REFRESH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        mContext = this;

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //天气不是每时每刻都在刷新，因此在后台定时刷新
        String cache = CacheUtils.getCache(this, Config.CITY_WEATHER);
        if(!TextUtils.isEmpty(cache)){
            parseData(cache);
        }else{
            getDataFromServer(FIRST);
        }

        setListener();
    }

    private void setListener() {
        mAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String provinceCache = CacheUtils.getCache(WeatherActivity.this, Config.PROVINCE_CITY);
                if(!TextUtils.isEmpty(provinceCache)){
                    parseCities(provinceCache);
                }else{
                    getPopWindowData();
                }
            }
        });

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromServer(REFRESH);
            }
        });
    }

    /*
    获取省市信息
     */
    private void getPopWindowData() {
        String citiesUrl = Config.LOGIN_URL + Config.CITIES;
        OkHttpUtils.get()
                .url(citiesUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CacheUtils.setCache(mContext, Config.PROVINCE_CITY, response);
                        parseCities(response);
                    }
                });
    }

    /*
    解析获取的省市信息
     */
    private void parseCities(String response) {
        View popView = View.inflate(this, R.layout.weather_pop_layout, null);
        mProvinceList = (ListView) popView.findViewById(R.id.city_province);
        mCitiesList = (ListView) popView.findViewById(R.id.city);

        Gson gson = new Gson();
        final List<ProvinceBean.Province> provinces = gson.fromJson(response, ProvinceBean.class).data;
        provinceName = new ArrayList<>();
        for(int i = 0; i < provinces.size(); i++){
            provinceName.add(provinces.get(i).areaName);
        }
        /*
        设置省份
         */
        ArrayAdapter provinceAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, provinceName);
        mProvinceList.setAdapter(provinceAdapter);
        mProvinceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //直辖市直接刷新数据
                for(int i = 0; i < mMunicipalities.length; i++){
                    if(provinces.get(position).areaName.equals(mMunicipalities[i])){
                        initCity = provinces.get(position).areaName;
                        mUrl = Config.WEATHER_URL + initCity + Config.WEATHER_KEY;
                        getDataFromServer(REFRESH);
                        mPopWindow.dismiss();
                        return;
                    }
                }
                System.out.print("你好");
                citiesName = new ArrayList<>();
                for(int i = 0; i < provinces.get(position).cities.size(); i++){
                    citiesName.add(provinces.get(position).cities.get(i).areaName);
                }
                ArrayAdapter cityAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, citiesName);
                mProvinceList.setVisibility(View.GONE);
                mCitiesList.setAdapter(cityAdapter);
                mCitiesList.setVisibility(View.VISIBLE);
            }
        });

        /*
        城市的点击事件
         */
        mCitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initCity = citiesName.get(position);
                mUrl = Config.WEATHER_URL + initCity + Config.WEATHER_KEY;
                getDataFromServer(REFRESH);
                mPopWindow.dismiss();
            }
        });

        View parent = this.getWindow().getDecorView();
        int height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
        mPopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, height);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setAnimationStyle(R.style.PopTheme);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /*
    从网络获取天气数据
     */
    public void getDataFromServer(final int flag) {
        OkHttpUtils.get()
                .url(mUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(WeatherActivity.this, "网络异常，请求失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(flag == REFRESH){
                            Toast.makeText(WeatherActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        }
                        CacheUtils.setCache(mContext, Config.CITY, initCity);
                        CacheUtils.setCache(mContext, Config.CITY_WEATHER, response);
                        Logger.d(response);
                        parseData(response);
                    }
                });
    }

    private void parseData(String response) {
        Gson gson = new Gson();
        mData = gson.fromJson(response, WeatherBean.class).result.data;

        String[] chuanyi = mData.life.info.chuanyi;
        mTextDate.setText(mData.realtime.time);
        mTextCity.setText(mData.realtime.city_name);
        mTextDeg.setText(mData.realtime.weather.temperature);
        mTextWind.setText(mData.realtime.wind.direct);
        mTextWindPower.setText(mData.realtime.wind.windspeed);
        mTextTitle.setText(mData.realtime.weather.info);
        mTextChuanyi.setText(chuanyi[0]);
         /*
        底部一周信息
         */
        mTextDay1.setText(ZHOU + mData.weather.get(0).week);
        mTextDay2.setText(ZHOU + mData.weather.get(1).week);
        mTextDay3.setText(ZHOU + mData.weather.get(2).week);
        mTextDay4.setText(ZHOU + mData.weather.get(3).week);
        mTextDay5.setText(ZHOU + mData.weather.get(4).week);

        /*
        如果当前时间大于18点显示晚上图片
         */
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d("hour", hour+"");
        if(hour >= 18 || hour < 5){
            Glide.with(this)
                    .load(Config.LOGIN_URL + "weatherdata/night.jpg")
                    .centerCrop()
                    .crossFade()
                    .into(mImage);
        }else{
            Glide.with(this)
                    .load(Config.LOGIN_URL + "weatherdata/day.jpg")
                    .centerCrop()
                    .crossFade()
                    .into(mImage);
        }

        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }
}
