package com.rentalphang.runj.listener;

/**
 * 为RecycleView添加点击事件
 *
 *
 */
public interface OnRecyclerViewListener {

    void onItemClick(int position);

    boolean onItemLongClick(int position);

    void onChildClick(int position, int childId);
}
