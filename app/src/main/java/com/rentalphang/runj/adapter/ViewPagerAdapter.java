package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.rentalphang.runj.R;
import java.util.List;



public class ViewPagerAdapter extends PagerAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private ImageLoader imageLoader;

    private DisplayImageOptions options;

    private List<String> imgUrls;

    public ViewPagerAdapter(Context context, List<String> imgUrls){

        this.context = context;

        this.imgUrls = imgUrls;

        this.layoutInflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.default_no_picture)
                .showImageOnFail(R.mipmap.default_no_picture)
                .showImageForEmptyUri(R.mipmap.default_no_picture)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount() {
        return imgUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.item_viewpager_layout,container,false);

        ImageView imageView = (ImageView) view.findViewById(R.id.viewpager_item_img);

        ImageLoader.getInstance().displayImage(imgUrls.get(position),imageView,options);

        container.addView(view,0);

        return view;
    }
}
