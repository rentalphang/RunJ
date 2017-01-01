package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.Comment;


import java.util.List;




public class CommentListAdapter extends BaseAdapter {

    private List<Comment> data;

    private Context context;

    private LayoutInflater layoutInflater;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private DisplayImageOptions circleOptions;

    public CommentListAdapter(Context context,List<Comment> data){
        this.data = data;
        this.context = context;
        this.layoutInflater  = LayoutInflater.from(context);

        initOptions();
    }

    private void initOptions(){

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload_head_pic)
                .showImageOnFail(R.mipmap.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload_head_pic)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.mipmap.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_listview_comment_layout,parent,false);
            viewHolder.headImg = (ImageView) convertView.findViewById(R.id.comment_head_img);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.comment_name_text);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.comment_time_text);
            viewHolder.content = (TextView) convertView.findViewById(R.id.comment_content_text);
            viewHolder.toLayout = (LinearLayout) convertView.findViewById(R.id.comment_reply_layout);
            viewHolder.toUserName = (TextView) convertView.findViewById(R.id.comment_toUser_name_text);
            viewHolder.toContent = (TextView) convertView.findViewById(R.id.comment_reply_content_text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment = data.get(position);
        if (comment.getFromUser()!= null) {
            imageLoader.displayImage(comment.getFromUser().getHeadImgUrl(),viewHolder.headImg,circleOptions);

            viewHolder.nameText.setText(comment.getFromUser().getNickName());

            viewHolder.timeText.setText(comment.getCreatedAt());
        }

        if (comment.getToUser()!=null) {
            viewHolder.toLayout.setVisibility(View.VISIBLE);
            viewHolder.content.setVisibility(View.GONE);
            viewHolder.toUserName.setText(comment.getToUser().getNickName());
            viewHolder.toContent.setText(comment.getContent());

        } else {
            viewHolder.toLayout.setVisibility(View.GONE);
            viewHolder.content.setVisibility(View.VISIBLE);
            viewHolder.content.setText(comment.getContent());
        }

        return convertView;
    }

    private class ViewHolder {

        private ImageView headImg;

        private TextView nameText;

        private TextView timeText;

        private TextView content;

        private LinearLayout toLayout;

        private TextView toUserName;

        private TextView toContent;

    }
}
