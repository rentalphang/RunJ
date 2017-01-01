package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;

/**
 * Created by rentalphang on 2016/12/29.
 */

public class RadarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView ivAvater;
    public TextView tvUserName;
    public TextView tvDistance;
    public TextView tvRunDistance;
    public Context context;
    public OnRecyclerViewListener listener;
    public DisplayImageOptions circleOptions;

    public RadarViewHolder(View itemView, Context context, OnRecyclerViewListener listener) {
        super(itemView);

        this.context = context;
        this.listener = listener;
        ivAvater = (ImageView) itemView.findViewById(R.id.iv_radar_item_avater);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_radar_item_username);
        tvDistance = (TextView) itemView.findViewById(R.id.tv_radar_item_distance);
        tvRunDistance = (TextView) itemView.findViewById(R.id.tv_radar_item_rundistance);
        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
