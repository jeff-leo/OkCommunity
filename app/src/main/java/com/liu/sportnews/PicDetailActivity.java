package com.liu.sportnews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.StatusUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PicDetailActivity extends AppCompatActivity{

    @BindView(R.id.pic_pager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    //图片总数
    @BindView(R.id.all_page_text)
    TextView mPageCount;
    //图片总数
    @BindView(R.id.current_page_text)
    TextView mCurrentPage;
    @BindView(R.id.pic_progress)
    ProgressBar mProgress;

    private String[] mPicArray;
    private PicAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_pic_detail);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        Bundle bundle = getIntent().getExtras().getBundle("picData");
        if (bundle != null) {
            mPicArray = bundle.getStringArray("picArray");
        }
        //设置总页数
        mPageCount.setText(mPicArray.length + "");

        mAdapter = new PicAdapter(this);
        initViewPager();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
    }

    private void initViewPager() {
        //图片滑动，当前页数指示
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.download:
                break;
            case R.id.collect:
                break;
        }
        return true;
    }

    class PicAdapter extends PagerAdapter {

        private Context mContext;

        public PicAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mPicArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            mProgress.setVisibility(View.VISIBLE);
            PhotoView photoView = new PhotoView(mContext);
            //设置photoView单击退出照片查看器
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    ((Activity) mContext).finish();
                }

                @Override
                public void onOutsidePhotoTap() {
                }
            });

            String url = Config.PICTURE_URL + mPicArray[position];
            Glide.with(mContext)
                    .load(url)
                    .crossFade()
                    .into(new GlideDrawableImageViewTarget(photoView){
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            mProgress.setVisibility(View.GONE);
                        }
                    });

            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }
}
