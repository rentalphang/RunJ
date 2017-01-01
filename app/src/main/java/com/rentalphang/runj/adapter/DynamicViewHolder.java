package com.rentalphang.runj.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rentalphang.runj.R;
import com.rentalphang.runj.ui.NineGridLayout;




public class DynamicViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    public ImageView avatar;
    public TextView nickName;
    public TextView time;
    public NineGridLayout nineGridLayout;
    public TextView content;

    public TextView likeCount;
    public ImageView likeImage;
    public TextView commentCount;
    public RelativeLayout likeRelative;
    public RelativeLayout shareRelative;

    public boolean isClickFinish ;
    public DynamicViewHolder(View itemView) {

        super(itemView);
        this.isClickFinish = true;
        cardView = (CardView) itemView.findViewById(R.id.cardview);
        avatar = (ImageView) itemView.findViewById(R.id.image_avatar);
        nickName = (TextView) itemView.findViewById(R.id.text_nickname);
        time = (TextView) itemView.findViewById(R.id.text_time);
        nineGridLayout = (NineGridLayout) itemView.findViewById(R.id.nineGridLayout);
        content = (TextView) itemView.findViewById(R.id.text_content);

        likeCount = (TextView) itemView.findViewById(R.id.text_like_count);
        likeImage = (ImageView) itemView.findViewById(R.id.image_like);
        commentCount = (TextView) itemView.findViewById(R.id.text_comment_count);
        likeRelative = (RelativeLayout) itemView.findViewById(R.id.relative_like);
        shareRelative = (RelativeLayout) itemView.findViewById(R.id.relative_share);

    }
}
