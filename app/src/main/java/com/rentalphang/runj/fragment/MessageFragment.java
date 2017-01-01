package com.rentalphang.runj.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.ChatActivity;
import com.rentalphang.runj.activity.SearchUserActivity;
import com.rentalphang.runj.adapter.NewsRecyclerAdapter;
import com.rentalphang.runj.listener.OnRecyclerViewListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.MessageListHandler;

/**
 * Created by rentalphang on 2016/7/29.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{

    private View mRootView;
    private LinearLayout newsLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private RelativeLayout sysNoticeRelative;

    private NewsRecyclerAdapter adapter;
    private ImageView ivAdd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_message, null);
            initComponent();
            setListener();
        }

        ViewGroup parents = (ViewGroup) mRootView.getParent();
        if (parents != null) {
            parents.removeView(mRootView);
        }
        return mRootView;
    }


    private void initComponent() {
        newsLayout = (LinearLayout) mRootView.findViewById(R.id.news_layout);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.news_sw_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.news_recyclerview);
        ivAdd = (ImageView) mRootView.findViewById(R.id.iv_message_add_friend);
        ivAdd.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new NewsRecyclerAdapter(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }



    private void setListener() {

        newsLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                newsLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                refreshLayout.setRefreshing(true);
                query();

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();

            }
        });

        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                BmobIMConversation conversation = adapter.getItem(position);
                bundle.putSerializable("c", conversation);
                intent.putExtra(getActivity().getPackageName(), bundle);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(final int position) {

                new com.rentalphang.runj.ui.AlertDialog(getActivity(), "删除消息", "确定将删除这个消息？", true, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogButtonClick(int requestCode, boolean isPositive) {
                        if (isPositive) {
                            BmobIM.getInstance().deleteConversation(adapter.getItem(position));
                            adapter.remove(position);
                        }

                    }
                }).show();

                return true;
            }

            @Override
            public void onChildClick(int position, int childId) {

            }
        });
    }


    /**
     * 查询本地会话
     */
    public void query() {
        adapter.bindDatas(BmobIMClient.getInstance().loadAllConversation());
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        query();
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //解注册EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        //重新刷新列表
        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        adapter.notifyDataSetChanged();
    }

    /**
     * 注册消息接收事件
     *
     * @param event 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     *              2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        //重新获取本地消息并刷新列表
        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_message_add_friend :
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
                break;
        }

    }
}
