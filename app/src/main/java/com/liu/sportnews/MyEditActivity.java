package com.liu.sportnews;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liu.sportnews.bean.EditBean;
import com.liu.sportnews.bean.HeadImgBean;
import com.liu.sportnews.bean.InfoBean;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.DatabaseHelper;
import com.liu.sportnews.utils.InfoDBServerUtils;
import com.liu.sportnews.utils.SharedPrerensUtils;
import com.liu.sportnews.utils.ShowCityPopWindow;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;

public class MyEditActivity extends AppCompatActivity {

    @BindView(R.id.edit_list)
    ListView mList;

    @BindView(R.id.edit_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edit_image)
    LinearLayout mImageLayout;

    @BindView(R.id.image)
    CircleImageView mHeadImage;

    //头像图片的唯一地址
    private Uri mImageUri;
    private static final int TAKE_PHOTO = 1;
    private static final int CROP_PHOTO = 0;
    private String mImageUrl;
    private File mImageFile;
    private List<String> mImagePath;

    /*
    界面数据
     */
    private InfoBean mInfoBean;
    private EditAdapter mAdapter;

    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_edit);
        ButterKnife.bind(this);
        Logger.init("MyEditActivity");

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDatabaseHelper = InfoDBServerUtils.helper;
        List<String> rightText = InfoDBServerUtils.getDataFromDatabase();
        if (rightText != null && !rightText.isEmpty()) {
            parseData(rightText);
        }
        Logger.d("onCreate");
        setItemListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("onResume");
        getInfoFromServer();
    }

    /*
    从服务器获取账号信息
     */
    private void getInfoFromServer() {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_SEARCH_INFO)
                .addParams(Config.USERNAME, Config.login_name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyEditActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response != null) {
                            Gson gson = new Gson();
                            mInfoBean = gson.fromJson(response, InfoBean.class);
                            List<String> rightList = new ArrayList<>(Arrays.asList(
                                    Config.login_name, mInfoBean.nickname, mInfoBean.sex,
                                    mInfoBean.city, mInfoBean.underwrite, mInfoBean.headUrl));
                            SharedPrerensUtils.setString(MyEditActivity.this, Config.NICKNAME_KEY, mInfoBean.nickname);
                            SharedPrerensUtils.setString(MyEditActivity.this, Config.SEX_KEY, mInfoBean.sex);
                            parseData(rightList);
                        }
                    }
                });
    }

    /*
    解析数据
     */
    private void parseData(List<String> rightList) {
        List<EditBean> mInfoList = new ArrayList<>();
        //存储至数据库
        initDatabase(rightList);
        String[] leftList = {"账号", "昵称" , "性别", "城市", "个性签名"};//固定写死不变
        for (int i = 0; i < leftList.length; i++) {
            EditBean bean = new EditBean();
            bean.leftText = leftList[i];
            bean.rightText = rightList.get(i);
            mInfoList.add(bean);
        }
        //加载头像
        String headUrl = rightList.get(rightList.size() - 1);
        if (!TextUtils.isEmpty(headUrl)) {
            Glide.with(this)
                    .load(headUrl)
                    .centerCrop()
                    .crossFade()
                    .into(mHeadImage);
        }
        mAdapter = new EditAdapter(this, mInfoList);
        mList.setAdapter(mAdapter);
    }

    /*
    存储账号信息至数据库
     */
    private void initDatabase(List<String> infoData) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", infoData.get(1));
        values.put("sex", infoData.get(2));
        values.put("city", infoData.get(3));
        values.put("underwrite", infoData.get(4));
        values.put("headUrl", infoData.get(5));
        database.update("Info", values, "username = ?", new String[]{Config.login_name});
    }

    private void setItemListener() {
        mImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEditActivity.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);//设置显示照相机
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (position) {
                    case 1://上传昵称
                        Intent intentNick = new Intent(MyEditActivity.this, EditInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "编辑昵称");
                        bundle.putString("hint", "取一个拉风的名字更容易让别人记住");
                        bundle.putString("type", "nickname");
                        intentNick.putExtra("bundle", bundle);
                        startActivity(intentNick);
                        break;
                    case 2://上传性别
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyEditActivity.this);
                        final String[] items = {"男", "女"};
                        builder.setTitle("性别").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upLoadInfoToServer("sex", items[which]);
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).show();
                        break;
                    case 3://上传地区信息
                        ShowCityPopWindow showPop = new ShowCityPopWindow();
                        View parentView = MyEditActivity.this.getWindow().getDecorView();
                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                        int height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
                        showPop.showCities(MyEditActivity.this, parentView, width, height);
                        showPop.setOnCitySelectedListener(new ShowCityPopWindow.OnCitySelectedListener() {
                            @Override
                            public void onCitySelected(String cityName) {
                                upLoadInfoToServer("city", cityName);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    case 4://上传签名
                        Intent intentUw = new Intent(MyEditActivity.this, EditInfoActivity.class);
                        Bundle bundleUw = new Bundle();
                        bundleUw.putString("title", "编辑个性签名");
                        bundleUw.putString("hint", "");
                        bundleUw.putString("type", "underwrite");
                        intentUw.putExtra("bundle", bundleUw);
                        startActivity(intentUw);
                        break;
                }
            }
        });
    }

    /*
    上传信息至服务器
     */
    public void upLoadInfoToServer(String type, String info) {
        OkHttpUtils.post()
                .url(Config.LOCAL_URL)
                .addParams(Config.ACTION, Config.ACTION_UPLOAD_INFO)
                .addParams(Config.USERNAME, Config.login_name)
                .addParams("type", type)
                .addParams("info", info)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyEditActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jObject;
                        int status = 0;
                        try {
                            jObject = new JSONObject(response);
                            status = jObject.getInt(Config.STATUS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (status == 1) {
                            Toast.makeText(MyEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            getInfoFromServer();//上传完数据马上获取数据刷新界面
                        } else {
                            Toast.makeText(MyEditActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImagePath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (mImagePath != null) {
                        mImageFile = new File(mImagePath.get(0));
                        mImageUri = Uri.fromFile(mImageFile);

                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(mImageUri, "image/*");
                        intent.putExtra("scale", true);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        startActivityForResult(intent, CROP_PHOTO);
                    }
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    upLoadImageToServer();
                }
                break;
        }
    }

    /*
     为每一次的头像生成不同的名，以免Glide不显示重复名字的资源
      */
    private void upLoadImageToServer() {
        Random random = new Random();
        int ranNumber = random.nextInt(10000);
        String headImgName = Config.login_name + ranNumber + ".jpg";

        OkHttpUtils.post()
                .addFile("mFile", headImgName, mImageFile)
                .url(Config.UPLOAD_HEAD_IMG_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyEditActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mImageUrl = Config.LOGIN_URL + response;
                        upLoadInfoToServer("headUrl", mImageUrl);
                        getInfoFromServer();
                        SharedPrerensUtils.setString(MyEditActivity.this, Config.HEAD_URL_KEY, mImageUrl);
                        EventBus.getDefault().post(new HeadImgBean(mImageUrl));
                    }
                });
    }


    public class EditAdapter extends BaseAdapter {

        private Context context;
        private List<EditBean> editList;

        public EditAdapter(Context context, List<EditBean> editList) {
            this.context = context;
            this.editList = editList;
        }

        @Override
        public int getCount() {
            return editList.size();
        }

        @Override
        public EditBean getItem(int position) {
            return editList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.center_item, parent, false);
                holder = new ViewHolder();
                holder.leftText = (TextView) view.findViewById(R.id.left_text);
                holder.rightText = (TextView) view.findViewById(R.id.right_text);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            EditBean item = getItem(position);
            holder.leftText.setText(item.leftText);
            holder.rightText.setText(item.rightText);
            return view;
        }

        class ViewHolder {
            TextView leftText;
            TextView rightText;
        }
    }
}
