package com.liu.sportnews.fragment.basefragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liu.sportnews.MainActivity;
import com.liu.sportnews.R;
import com.liu.sportnews.fragment.CategoryFragment;
import com.liu.sportnews.fragment.FoundFragment;
import com.liu.sportnews.fragment.MainNewsFragment;

import java.util.ArrayList;

/**
 * Created by Liujianfeng on 2016/7/19.
 */
public class HomeFragment extends Fragment{

    private ViewPager mViewPager;

    //fragment的集合
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    //fragment的标签集合
    private ArrayList<String> mLableList = new ArrayList<String>();

    private static Context mContext;
    private static FragmentManager mManager;

    private MainActivity mActivity;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    public static HomeFragment newInstance(Context context, FragmentManager manager){
        HomeFragment homeFragment = new HomeFragment();
        mContext = context;
        mManager = manager;
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homefrag_layout, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.content_pager);

        mActivity = (MainActivity) mContext;
        mTabLayout = mActivity.getTabLayout();
        mToolbar = mActivity.getToolbar();
        initPager();
        initFragment();
        return view;
    }

    //初始化Tablayou,viewpager，等
    private void initPager() {

        mFragmentList.add(MainNewsFragment.newInstance(mContext));
        mFragmentList.add(CategoryFragment.newInstance(mContext));
        mFragmentList.add(FoundFragment.newInstance(mContext));

        mLableList.add("主页");
        mLableList.add("分类");
        mLableList.add("发现");

    }

    public void initFragment() {
        ContentPagerAdp pagerAdp = new ContentPagerAdp(mManager);

        //设置Tablayout的模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //添加Tab
        for (int i = 0; i < mFragmentList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mLableList.get(i)));
        }

        mViewPager.setAdapter(pagerAdp);
        mTabLayout.setupWithViewPager(mViewPager);

        //切换Tab时的监听
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(mLableList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //主fragment适配器
    class ContentPagerAdp extends FragmentPagerAdapter {

        public ContentPagerAdp(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        //刷新fragment，我博客中记录了
        @Override
        public long getItemId(int position) {
            int hashCode = mFragmentList.get(position).hashCode();
            return hashCode;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mLableList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }
}
