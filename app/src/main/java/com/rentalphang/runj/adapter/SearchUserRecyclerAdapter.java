package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewClickListener;
import com.rentalphang.runj.model.bean.User;

import java.util.ArrayList;
import java.util.List;


public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.ViewHolder> {

    private OnRecyclerViewClickListener listener = null;
    private DisplayImageOptions circleOptions;
    private List<User> data = new ArrayList<>();

    private List<User> followData = new ArrayList<>();

    private User user = null;
    private Context context;

    public void setData(List<User> data) {
        this.data = data;
    }

    public List<User> getData() {
        return data;
    }

    public void setFollowData(List<User> followData) {
        this.followData = followData;
    }

    public SearchUserRecyclerAdapter(User user, OnRecyclerViewClickListener listener) {
        this.user = user;
        this.listener = listener;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader.getInstance().displayImage(data.get(position).getHeadImgUrl(), holder.avatar, circleOptions);
        holder.nickName.setText(data.get(position).getNickName());
    }

    @Override
    public int getItemCount() {
        return data == null || data.size() <= 0 ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView avatar;
        private TextView nickName;
        private Button chat;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.iv_search_avatar);
            nickName = (TextView) itemView.findViewById(R.id.tv_search_nickname);
            chat= (Button) itemView.findViewById(R.id.bt_search_chat);
            itemView.setOnClickListener(this);
            chat.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(getAdapterPosition());
            }
        }
    }
}
