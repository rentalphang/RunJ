package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;

import java.util.List;

/**
 * Created by rentalphang on 2016/12/29.
 */

public class RadarRecycleAdapter extends RecyclerView.Adapter<RadarViewHolder> {
    public List<RadarNearbyInfo> list;
    public List<String> avaterList;
    public OnRecyclerViewListener listener;
    public Context context;
    public DisplayImageOptions circleOptions;

    public RadarRecycleAdapter(List<RadarNearbyInfo> list) {
        this.list = list;
//        this.avaterList = avaterList;
        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    @Override
    public RadarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View radarView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radar_user_info,parent,false);
        return new RadarViewHolder(radarView,context,listener);
    }

    @Override
    public void onBindViewHolder(RadarViewHolder holder, int position) {
        if(list == null || list.size() == 0){

        }else{
            holder.tvUserName.setText(String.valueOf(list.get(position).userID));
            holder.tvRunDistance.setText(String.valueOf(list.get(position).comments));
            holder.tvDistance.setText("附近" + String.valueOf(list.get(position).distance) + "米");
//            //不直接display imageview改为ImageAware，解决ListView滚动时重复加载图片
//            ImageAware imageAware = new ImageViewAware(holder.ivAvater,false);
//            ImageLoader.getInstance().displayImage(avaterList.get(position), imageAware, circleOptions);
        }

    }



    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }
}
