package com.rentalphang.runj.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rentalphang.runj.R;


public class RecyclerFootViewHolder extends RecyclerView.ViewHolder {

    public TextView loadMore;
    public LinearLayout linearLayout;
    public ProgressBar progressBar;

    public RecyclerFootViewHolder(View itemView) {

        super(itemView);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.linear);
        loadMore = (TextView) itemView.findViewById(R.id.text_load_more);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
}
