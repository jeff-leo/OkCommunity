package com.liu.sportnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liu.sportnews.NewsDetailActivity;
import com.liu.sportnews.R;
import com.liu.sportnews.bean.NewsBean.NewsDetailList;
import com.liu.sportnews.utils.Config;
import com.orhanobut.logger.Logger;
import com.viewpagerindicator.CirclePageIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujianfeng on 2016/7/9.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    //首页的所有数据
    private List<NewsDetailList> mHomeList;

    //首页轮播的头条新闻
    private List<NewsDetailList> mTopList = new ArrayList<>();

    //首页的list新闻
    private List<NewsDetailList> mItemList = new ArrayList<>();

    //头部的viewHolder
    //private HeaderViewHolder mHeaderViewHolder;
    //内容的viewholder
    //private MyViewHolder mViewHolder;

    //全局的handler
    public Handler mHandler;

    private HeaderAdapter mHeaddapter;

    public int mTopCurrentItem;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;

    private int mHeaderCount = 1;

    public RVAdapter(Context context, List<NewsDetailList> homeList, List<NewsDetailList> topList
            , List<NewsDetailList> itemList) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mHomeList = homeList;
        mTopList = topList;
        mItemList = itemList;
    }

    public int getContentCount() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaderCount && mHeaderCount != 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(mInflater.inflate(R.layout.rvheader, parent, false));
        } else if(viewType == TYPE_CONTENT){
            return new MyViewHolder(mInflater.inflate(R.layout.rv_item, parent, false));
        }

        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, NewsDetailList beanData);
    }

    OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        //头部
        if (position == 0) {

            if (mHeaddapter == null) {
                mHeaddapter = new HeaderAdapter(mContext);
                ((HeaderViewHolder)holder).headerPager.setAdapter(mHeaddapter);

                //设置indicator
                ((HeaderViewHolder)holder).topIndicator.setViewPager(((HeaderViewHolder) holder).headerPager);
                ((HeaderViewHolder)holder).topText.setText(mTopList.get(0).title);

                ((HeaderViewHolder)holder).topIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        ((HeaderViewHolder) holder).topText.setText(mTopList.get(position).title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });


                //利用handler循环发送消息
                mHandler = new Handler() {
                    public void handleMessage(Message msg) {

                        mTopCurrentItem = ((HeaderViewHolder) holder).headerPager.getCurrentItem();

                        if (mTopCurrentItem < mTopList.size() - 1) {
                            mTopCurrentItem++;
                        } else {
                            mTopCurrentItem = 0;
                        }

                        ((HeaderViewHolder) holder).headerPager.setCurrentItem(mTopCurrentItem, false);
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                    }
                };

                mHandler.sendEmptyMessageDelayed(0, 3000);

                ((HeaderViewHolder)holder).topText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra(Config.NEWS_DATA, mTopList.get(mTopCurrentItem));
                        mContext.startActivity(intent);
                    }
                });
            }

        } else {
            //mViewHolder.setIsRecyclable(false);只是避免了回收问题，但没有解决问题
            final NewsDetailList currentItem = mItemList.get(position-1);

            ((MyViewHolder)holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v, currentItem);
                }
            });

            String imageUrl = mItemList.get(position-1).thumbnail_pic_s;
            ImageView imageView = ((MyViewHolder)holder).imageView;

            Glide.with(mContext)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.defaultpic)
                    .crossFade()
                    .into(imageView);

            //加载标题等
            ((MyViewHolder)holder).news.setText(mItemList.get(position-1).title);
            ((MyViewHolder)holder).author.setText(mItemList.get(position-1).author_name);
            ((MyViewHolder)holder).date.setText(mItemList.get(position-1).date);
        }

    }

    @Override
    public int getItemCount() {
        return getContentCount() + mHeaderCount;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item;
        ImageView imageView;
        TextView news;
        TextView author;
        TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.cv_id);
            imageView = (ImageView) itemView.findViewById(R.id.cv_image);
            news = (TextView) itemView.findViewById(R.id.cv_text);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
        }

    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager headerPager;
        private CirclePageIndicator topIndicator;
        private TextView topText;
        private RelativeLayout headerLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerPager = (ViewPager) itemView.findViewById(R.id.header_viewpager);
            topIndicator = (CirclePageIndicator) itemView.findViewById(R.id.top_indicator);
            topText = (TextView) itemView.findViewById(R.id.top_text);
            headerLayout = (RelativeLayout) itemView.findViewById(R.id.header_layout);
        }
    }


    //轮播图片的适配器
    class HeaderAdapter extends PagerAdapter {
        Context mContext;

        public HeaderAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mTopList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView topImage = new ImageView(mContext);
            topImage.setScaleType(ImageView.ScaleType.FIT_XY);
            String topImageUrl = mTopList.get(position).thumbnail_pic_s02;

            Glide
                    .with(mContext)
                    .load(topImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.login)
                    .crossFade()
                 .into(topImage);

            container.addView(topImage);

            return topImage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
