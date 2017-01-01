package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;



/**
 * 接收到的文本类型
 *
 *
 */
public class ReceiveTextHolder extends BaseViewHolder {

    private TextView time;
    private ImageView avatar;
    private TextView message;

    private Context context;
    private OnRecyclerViewListener listener;
    private DisplayImageOptions circleOptions;

    public ReceiveTextHolder(View itemView,Context context,OnRecyclerViewListener listener) {
        super(itemView,context,listener);

        this.context = context;

        this.avatar = (ImageView) itemView.findViewById(R.id.chat_avatar);
        this.time = (TextView) itemView.findViewById(R.id.chat_time_text);
        this.message = (TextView) itemView.findViewById(R.id.chat_message);

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

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage)o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        this.time.setText(time);
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        if (info != null) {
            if (!TextUtils.isEmpty(info.getAvatar())){
                if (!info.getAvatar().equals(avatar.getTag())){//增加tag标记，减少UIL的display次数
                    avatar.setTag(info.getAvatar());
                    //不直接display imageview改为ImageAware，解决ListView滚动时重复加载图片
                    ImageAware imageAware = new ImageViewAware(avatar,false);
                    ImageLoader.getInstance().displayImage(info.getAvatar(),imageAware,circleOptions);
                }
            }
        } else { //默认

            String defaultUrl = ImageDownloader.Scheme.DRAWABLE.wrap("R.drawable.default_avatar_blue");
            ImageAware imageAware = new ImageViewAware(avatar,false);
            ImageLoader.getInstance().displayImage(defaultUrl,imageAware,circleOptions);
//
//            avatar.setImageResource(R.drawable.head);
        }
        String content =  message.getContent();
        this.message.setText(content);

        this.message.setOnClickListener(this);
        this.message.setOnLongClickListener(this);
    }


    public void showTime(boolean isShow) {
        this.time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAnimation() {

    }
}
