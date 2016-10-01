package com.liu.sportnews.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liu.sportnews.R;
import com.liu.sportnews.bean.ProvinceBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 封装的PopWindow选择器
 */
public class ShowCityPopWindow {

    private ListView mProvinceList;
    private ListView mCitiesList;
    private PopupWindow mPopWindow;

    private List<String> provinceName;//省名
    private List<String> citiesName;//市名

    private Context mContext;
    private View mParentView;//popWind的父布局
    private int mWidth;
    private int mHeight;

    public void showCities(Context context, View parent, int width, int height){
        mContext = context;
        mParentView = parent;
        mWidth = width;
        mHeight = height;

        String provinceCache = CacheUtils.getCache(mContext, Config.PROVINCE_CITY);
        if(!TextUtils.isEmpty(provinceCache)){
            parseCities(provinceCache);
        }else{
            getPopWindowData();
        }
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
                        Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
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
        View popView = View.inflate(mContext, R.layout.weather_pop_layout, null);
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
                String cityName = citiesName.get(position);
                mOnCitySelectedListener.onCitySelected(cityName);
                mPopWindow.dismiss();
            }
        });

        mPopWindow = new PopupWindow(popView, mWidth, mHeight);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mPopWindow.setAnimationStyle(R.style.PopTheme);
        mPopWindow.setBackgroundDrawable(dw);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
    }

    /*
    选择城市的回调,返回城市名
     */
    public interface OnCitySelectedListener{
        void onCitySelected(String cityName);
    }

    public OnCitySelectedListener mOnCitySelectedListener;

    public void setOnCitySelectedListener(OnCitySelectedListener onCitySelectedListener){
        mOnCitySelectedListener = onCitySelectedListener;
    }
}
