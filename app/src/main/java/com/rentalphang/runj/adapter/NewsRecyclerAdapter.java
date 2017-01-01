package com.rentalphang.runj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.rentalphang.runj.R;
import com.rentalphang.runj.listener.OnRecyclerViewListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;


/**
 * 消息适配器
 *
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<BmobIMConversation> conversations = new ArrayList<>();

    private OnRecyclerViewListener listener;
    private Context context;

    public NewsRecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 绑定数据
     * @param list
     */
    public void bindDatas(List<BmobIMConversation> list) {
        conversations.clear();
        if (null != list) {
            conversations.addAll(list);
        }
    }

    /**移除会话
     * @param position
     */
    public void remove(int position){
        conversations.remove(position);
        notifyDataSetChanged();
    }

    /**获取会话
     * @param position
     * @return
     */
    public BmobIMConversation getItem(int position){
        return conversations.get(position);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_recycler_layout,parent,false);

        return new NewsViewHolder(v,context,listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((NewsViewHolder)holder).bindData(conversations.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener listener) {
        this.listener = listener;
    }
}
