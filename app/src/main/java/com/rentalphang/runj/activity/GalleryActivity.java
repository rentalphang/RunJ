package com.rentalphang.runj.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.ui.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

import static com.baidu.location.h.j.p;
import static com.mob.commons.eventrecoder.EventRecorder.clear;

/**
 * Created by rentalphang on 2016/12/13.
 */

public class GalleryActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.iv_gallery_delete)
    public ImageView ivDelete;

    @Bind(R.id.iv_gallery_back)
    public ImageView ivBack;

    @Bind(R.id.tv_gallery_position)
    public TextView tvPosition;

    @Bind(R.id.gallery)
    public ViewPagerFixed pager;


    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private Intent intent;



    private MyPageAdapter adapter;
    private List<String> pictureList;//图片URL

    private DisplayImageOptions options;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        intent = getIntent();
        pictureList = intent.getStringArrayListExtra("pictureList");
        position = Integer.parseInt(intent.getStringExtra("position"));
        initOptions();
        initComponent();

    }

    private void initComponent(){
        ivDelete.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < pictureList.size(); i++) {
            initListViews( pictureList.get(i));
        }
    }

    private void initOptions(){

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_no_picture)
                .showImageOnFail(R.mipmap.default_no_picture)
                .showImageForEmptyUri(R.mipmap.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private void initListViews(String url) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        imageLoader.displayImage(url,img, options);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_gallery_delete:
               if (listViews.size() == 1) {
                    pictureList.clear();
//                    tvPosition.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
                    Intent intent = new Intent("data.broadcast.action");
                    sendBroadcast(intent);
                    finish();
                } else {
                    pictureList.remove(position);
                    pager.removeAllViews();
                    listViews.remove(location);
                    adapter.setListViews(listViews);
//                    send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
                    adapter.notifyDataSetChanged();
                }
                break;

        }

    }

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;
        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
