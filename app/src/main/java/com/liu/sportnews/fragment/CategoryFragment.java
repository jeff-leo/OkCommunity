package com.liu.sportnews.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liu.sportnews.CategoryActivity;
import com.liu.sportnews.MainActivity;
import com.liu.sportnews.R;
import com.liu.sportnews.utils.Config;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Liujianfeng on 2016/7/9.
 */
public class CategoryFragment extends Fragment {

    public static Context mContext;

    private RecyclerView mRecyclerView;

    /*
    上线前转移到服务器
     */
    private String[] mCategory = {"社会", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚"};
    private String[] mImages = {"http://image60.360doc.com/DownloadImg/2013/04/1614/31674447_14.jpg",
            "http://www.lvhaiyan.com/uploads/allimg/130219/1-13021Z10936.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/08f790529822720e5eec3a667bcb0a46f21fab08.jpg",
            "http://imgsrc.baidu.com/forum/pic/item/f4d3572c11dfa9ec295de2ed62d0f703908fc1dd.jpg",
            "http://pic1.win4000.com/wallpaper/c/546c57d72ca1d.jpg",
            "http://5.26923.com/download/pic/000/335/5ab7a4b74b9e941a0082c8cdcd3c0906.jpg",
            "http://www.bz55.com/uploads/allimg/150825/139-150R5154J6.jpg",
            "http://home.ebrun.com/jeditor/php/upload/20140318/13951290274077.jpg.jpg",
            "http://image52.360doc.com/DownloadImg/2012/06/0814/24708350_13.jpg"
    };

    private String[] mCategoryId = {Config.SHEHUI, Config.GUONEI, Config.GUOJI, Config.YULE,
            Config.TIYU, "", Config.KEJI, Config.CAIJING, Config.SHISHANG, Config.JUNSHI};

    public static CategoryFragment newInstance(Context context) {
        CategoryFragment categoryFragment = new CategoryFragment();
        mContext = context;
        return categoryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.category_layout, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.cate_recycle);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        CategoryAdapter adapter = new CategoryAdapter();
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    //Category的适配器
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.cateitem_layout, parent, false);
            return new CategoryViewHolder(view);
        }


        @Override
        public void onBindViewHolder(CategoryViewHolder holder, final int position) {
            holder.textView.setText(mCategory[position]);

            Glide.with(mContext)
                    .load(mImages[position])
                    .centerCrop()
                    .placeholder(R.drawable.login)
                    .crossFade()
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CategoryActivity.class);

                    //传送的数据
                    String imageUrl = mImages[position];//分类的背景
                    String text = mCategory[position];//分类的标题
                    String cateUrl = null;

                    Bundle bundle = new Bundle();
                    bundle.putString("imageUrl", imageUrl);
                    bundle.putString("categoryText", text);

                    cateUrl = Config.REQUEST_URL + mCategoryId[position] + Config.KEY +
                            Config.APP_KEY;//具体分类新闻的url
                    System.out.print("分类frag" + cateUrl);
                    bundle.putString("categoryUrl", cateUrl);

                    intent.putExtra("data", bundle);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mCategory.length;
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.cate_image);
                textView = (TextView) itemView.findViewById(R.id.cate_text);
            }

        }
    }


}
