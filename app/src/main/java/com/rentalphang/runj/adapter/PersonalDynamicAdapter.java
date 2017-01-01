package com.rentalphang.runj.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;
import com.rentalphang.runj.model.bean.Dynamic;
import com.rentalphang.runj.utils.GeneralUtil;

import java.util.List;


public class PersonalDynamicAdapter extends RecyclerView.Adapter<PersonalDynamicViewHolder>{


    private OnRecyclerViewListener listener;
    private List<Dynamic> data;
    public PersonalDynamicAdapter(OnRecyclerViewListener listener) {
        this.listener = listener;
    }

    public void setData(List<Dynamic> data) {
        this.data = data;
    }

    @Override
    public PersonalDynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_personal_dynamic, parent, false);

        return new PersonalDynamicViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(PersonalDynamicViewHolder holder, int position) {

            Dynamic dynamic = data.get(position);
            holder.dayText.setText(GeneralUtil.getDayFromDate(dynamic.getCreatedAt()));
            holder.monthText.setText(GeneralUtil.getMonthFromDate(dynamic.getCreatedAt()));
            if (dynamic.getContent() != null && dynamic.getContent().length() > 0) {
                holder.contentText.setVisibility(View.VISIBLE);
                holder.contentText.setText(dynamic.getContent());
            } else {
                holder.contentText.setVisibility(View.GONE);
            }

            if ( dynamic.getImage() != null&&dynamic.getImage().size() > 0 ) {

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.mipmap.default_no_picture)
                        .showImageOnFail(R.mipmap.default_no_picture)
                        .showImageForEmptyUri(R.mipmap.default_no_picture)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

                ImageLoader.getInstance().displayImage(dynamic.getImage().get(0), holder.picture, options);
                if (dynamic.getImage().size() > 1) {
                    holder.number.setVisibility(View.VISIBLE);
                    holder.number.setText("共" + dynamic.getImage().size() + "张");
                } else {
                    holder.number.setVisibility(View.GONE);
                }
            } else {
                holder.picture.setVisibility(View.GONE);
                holder.number.setVisibility(View.GONE);
            }

            if (position != 0 && GeneralUtil.isSameDate(dynamic.getCreatedAt(), data.get(position - 1).getCreatedAt())) {
                holder.dayText.setVisibility(View.INVISIBLE);
                holder.monthText.setVisibility(View.INVISIBLE);
                holder.grayView.setVisibility(View.INVISIBLE);
                holder.sampleView.setVisibility(View.VISIBLE);
            } else if(position == 0) {
                holder.dayText.setVisibility(View.VISIBLE);
                holder.monthText.setVisibility(View.VISIBLE);
                holder.grayView.setVisibility(View.INVISIBLE);
            }else {
                holder.dayText.setVisibility(View.VISIBLE);
                holder.monthText.setVisibility(View.VISIBLE);
                holder.grayView.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public int getItemCount() {

        return data==null||data.size()<=0?0:data.size();
    }
}
