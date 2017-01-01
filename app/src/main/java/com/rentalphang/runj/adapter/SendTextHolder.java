package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;



/**
 *
 * 发送文本类型
 *
 * Created by 洋 on 2016/5/21.
 */
public class SendTextHolder extends BaseViewHolder{

    private ImageView avatar;
    private ImageView failResend;
    private TextView time;
    private TextView message;
    private TextView sendStatus;
    private ProgressBar progressLoad;

    private Context context;
    private OnRecyclerViewListener listener;
    private BmobIMConversation c;

    private DisplayImageOptions circleOptions;

    public SendTextHolder(View itemView, Context context, BmobIMConversation c, OnRecyclerViewListener listener) {
        super(itemView,context,listener);
        this.context = context;
        this.listener = listener;
        this.c = c;
        this.avatar = (ImageView) itemView.findViewById(R.id.chat_item_avatar);
        this.failResend = (ImageView) itemView.findViewById(R.id.chat_item_fail_resend);
        this.message = (TextView) itemView.findViewById(R.id.chat_item_message);
        this.time = (TextView) itemView.findViewById(R.id.chat_item_time);
        this.sendStatus = (TextView) itemView.findViewById(R.id.chat_item_send_status);
        this.progressLoad = (ProgressBar) itemView.findViewById(R.id.chat_item_progress_load);


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
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
//            avatar.setImageResource(R.drawable.default_head);
        }

        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        this.message.setText(content);
        this.time.setText(time);

        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {//发送失败
            this.failResend.setVisibility(View.VISIBLE);
            this.progressLoad.setVisibility(View.GONE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {//发送中
            this.failResend.setVisibility(View.GONE);
            this.progressLoad.setVisibility(View.VISIBLE);
        } else {//发送成功
            this.failResend.setVisibility(View.GONE);
            this.progressLoad.setVisibility(View.GONE);
        }

        this.message.setOnClickListener(this);
        this.message.setOnLongClickListener(this);

        //重发消息
        this.failResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage bmobIMMessage) {
                        progressLoad.setVisibility(View.VISIBLE);
                        failResend.setVisibility(View.GONE);
                        sendStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                        if(e==null){
                            sendStatus.setVisibility(View.VISIBLE);
                            sendStatus.setText("已发送");
                            failResend.setVisibility(View.GONE);
                            progressLoad.setVisibility(View.GONE);
                        }else{
                            failResend.setVisibility(View.VISIBLE);
                            progressLoad.setVisibility(View.GONE);
                            sendStatus.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void showTime(boolean isShow) {
        this.time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAnimation() {

    }
}
