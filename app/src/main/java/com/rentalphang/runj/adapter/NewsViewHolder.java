package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;
import com.rentalphang.runj.utils.GeneralUtil;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;



/**
 *
 */
public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {


    private ImageView avatar;
    private TextView name;
    private TextView time;
    private TextView message;
    private TextView unread;


    private Context context;
    private OnRecyclerViewListener listener;
    private DisplayImageOptions circleOptions;

    public NewsViewHolder(View itemView,Context context,OnRecyclerViewListener listener) {
        super(itemView);

        this.context = context;
        this.listener =listener;
        this.avatar = (ImageView) itemView.findViewById(R.id.news_item_recent_avatar);
        this.name = (TextView) itemView.findViewById(R.id.news_item_recent_name);
        this.time = (TextView) itemView.findViewById(R.id.news_item_recent_time);
        this.message = (TextView) itemView.findViewById(R.id.news_item_recent_msg);
        this.unread = (TextView) itemView.findViewById(R.id.news_item_recent_unread);

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
        itemView.setOnLongClickListener(this);
    }


    public void bindData(Object o) {
        BmobIMConversation conversation = (BmobIMConversation) o;
        List<BmobIMMessage> msgs = conversation.getMessages();

        if (msgs!= null && msgs.size()>0) {
            BmobIMMessage lastMsg = msgs.get(0);
            String content = lastMsg.getContent();
            this.message.setText(content);
            Log.i("TAG",content);
            Log.i("TAG",this.message.getText().toString());

            this.time.setText(GeneralUtil.getChatTime(false,lastMsg.getCreateTime()));

        } else {

            this.message.setText("");
            this.time.setText("");
        }

        if (!TextUtils.isEmpty(conversation.getConversationIcon())){

            if (!conversation.getConversationIcon().equals(avatar.getTag())){

                avatar.setTag(conversation.getConversationIcon());
                //不直接display imageview改为ImageAware，解决ListView滚动时重复加载图片
                ImageAware imageAware = new ImageViewAware(avatar,false);
                ImageLoader.getInstance().displayImage(conversation.getConversationIcon(), imageAware, circleOptions);
            }
        } else {
            String defaultUrl = ImageDownloader.Scheme.DRAWABLE.wrap("R.drawable.default_head");
            ImageAware imageAware = new ImageViewAware(avatar,false);
            ImageLoader.getInstance().displayImage(defaultUrl,imageAware,circleOptions);
//            this.avatar.setImageResource(R.drawable.head);
        }

        this.name.setText(conversation.getConversationTitle());
        Log.i("TAG",conversation.getConversationTitle());
        long unreadnumber = BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
        if(unreadnumber>0){
            unread.setVisibility(View.VISIBLE);
            unread.setText(String.valueOf(unreadnumber));
        }else{
            unread.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if (listener!= null) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            listener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}
