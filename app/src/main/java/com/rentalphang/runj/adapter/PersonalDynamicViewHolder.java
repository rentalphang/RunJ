package com.rentalphang.runj.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;


public class PersonalDynamicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView dayText;//日
    public TextView monthText;//月
    public ImageView picture;//图片
    public TextView number;//数量
    public TextView contentText;//内容
    public View grayView;//下划线
    public View sampleView;


    public OnRecyclerViewListener listener;

    public PersonalDynamicViewHolder(View itemView, OnRecyclerViewListener listener) {
        super(itemView);
        this.listener = listener;
        this.dayText = (TextView) itemView.findViewById(R.id.text_dynamic_day);
        this.monthText = (TextView) itemView.findViewById(R.id.text_dynamic_month);
        this.picture = (ImageView) itemView.findViewById(R.id.image_dynamic_picture);
        this.number = (TextView) itemView.findViewById(R.id.text_dynamic_count);
        this.contentText = (TextView) itemView.findViewById(R.id.text_dynamic_content);
        this.grayView = itemView.findViewById(R.id.view_dynamic_gray);
        this.sampleView = itemView.findViewById(R.id.view_dynamic_gray_sample);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(listener!=null) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(listener!= null) {
            listener.onItemLongClick(getAdapterPosition());
            return true;
        }
        return false;
    }
}
