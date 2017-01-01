package com.rentalphang.runj.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.PublishDynamicActivity;
import com.rentalphang.runj.adapter.DynamicRecyclerAdapter;
import com.rentalphang.runj.model.bean.Dynamic;
import com.rentalphang.runj.model.bean.Like;
import com.rentalphang.runj.model.bean.Timeline;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by rentalphang on 2016/7/29.
 */
public class CommunityFragment extends Fragment implements View.OnClickListener{

    private View mRootView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int request_code_Dynamic_details = 0x12;
    public static final int request_code_push_dynamic = 0x13;

    private String mParam1;
    private String mParam2;

    private Context context;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private User user;//当前用户

    private Timeline userTimeline;//当前用户的时间线

    private List<Dynamic> data ; //数据

    private DynamicRecyclerAdapter adapter;



    private int lastVisibleItemPosition ;
    private BmobDate loadTime = null;//加载时间

    private boolean isLoading = false; //加载更多

    private List<Dynamic> likes = new ArrayList<>(); //当前用户的点赞数

    private List<Like> likesList = new ArrayList<>();//当前用户的点赞记录

    private Handler handler = new Handler();
    private ImageView ivAdd;


    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        data = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ShareSDK.initSDK(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_community,null);
            user = BmobUser.getCurrentUser(context,User.class);
            initComponent(mRootView);
            if(user!= null) {
                //获取当前用户点赞的动态
                getLikes();
                //获取用户时间线
                getUserTimeline();
            }
        }

        ViewGroup parents = (ViewGroup) mRootView.getParent();
        if(parents != null){
            parents.removeView(mRootView);
        }
        return mRootView;
    }


    /**
     * 初始化组件
     * @param view
     */
    private void initComponent(View view) {
        ivAdd = (ImageView) view.findViewById(R.id.iv_dynamic_add);
        ivAdd.setOnClickListener(this);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_home_swiperefresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_dynamic);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DynamicRecyclerAdapter(getActivity(),this);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (userTimeline != null) {

                    if(GeneralUtil.isNetworkAvailable(context)) {//网络提供
                        //获取动态
                        refreshDynamic();

                        getLikes();
                    } else{
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(context,"没有网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition+1==adapter.getItemCount()) {
                    //手指上抛，最后可见的item位置+1等于适配器中数据个数，即最后一个Item可见
                    boolean isRefreshing = refreshLayout.isRefreshing();
                    if (isRefreshing) {//正在刷新
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        //显示加载更多布局
                        adapter.setIsLoadMore(true);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                                //延迟2秒加载数据
                                loadDynamic();
                            }
                        }, 2000);

                    }
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    /**
     * 初次刷新加载动态
     */
    private void initRefreshDynamic(){
        refreshLayout.setRefreshing(true);//开始刷新
        //刷新动态
        refreshDynamic();
    }

    /**
     * 下拉刷新查询动态
     */
    private void refreshDynamic(){

        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.addWhereRelatedTo("allDynamic", new BmobPointer(userTimeline));
        query.include("fromUser");
        query.order("-createdAt");
        query.setLimit(10);
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(final List<Dynamic> list) {

                if (list != null && list.size() > 0) {

                    //没有新动态
                    if(data!=null && data.size()>0&& data.get(0).equals(list.get(0))){

                        refreshLayout.setRefreshing(false);
                        Toast.makeText(context, "没有新动态", Toast.LENGTH_SHORT).show();

                    }else {

                        //清除data集合中数据
                        data.clear();
                        data = list;

                        refreshLayout.setRefreshing(false);
                        adapter.setData(data);
                        adapter.setIsLoadMore(false);
                        Toast.makeText(context, "刷新完成", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    refreshLayout.setRefreshing(false);
                    adapter.setIsLoadMore(false);
                    Toast.makeText(context, "没有动态，去发布一个吧！！", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onError(int i, String s) {
                Log.i("TAG", "s+i");
                refreshLayout.setRefreshing(false);
                Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();


            }
        });

    }
    /**
     * 上拉加载动态
     */
    private void loadDynamic(){

        BmobQuery<Dynamic> query = new BmobQuery<>();
        loadTime = new BmobDate(GeneralUtil.createdAtToDate(data.get(data.size() - 1).getCreatedAt()));
        query.addWhereLessThanOrEqualTo("createdAt",loadTime);
        query.order("-createdAt");
        query.addWhereRelatedTo("allDynamic", new BmobPointer(userTimeline));
        query.include("fromUser");
        query.setLimit(10);
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {
                if(list.size()>0 && list!= null){

                    for (Dynamic d:list) {
                        data.add(d);
                    }
                    refreshLayout.setRefreshing(false);
                    adapter.setData(data);
                    adapter.setIsLoadMore(false);
                }else {
                    refreshLayout.setRefreshing(false);
                    adapter.setIsLoadMore(false);
                    Toast.makeText(context, "已到最底部", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(int i, String s) {

                Log.i("TAG",s+i);
                refreshLayout.setRefreshing(false);
                adapter.setIsLoadMore(false);
                Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 获取用户对应时间线
     */
    private void getUserTimeline(){
        BmobQuery<Timeline> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", user);
        query.findObjects(context, new FindListener<Timeline>() {
            @Override
            public void onSuccess(List<Timeline> list) {
                if (list != null && list.size() == 1) {
                    userTimeline = list.get(0);
                    //初始化获取动态
                    initRefreshDynamic();

                }
            }
            @Override
            public void onError(int i, String s) {
                Log.i("TAG", s + i);
                refreshLayout.setRefreshing(false);
                Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取当前用户的点赞动态集合
     */
    private void getLikes(){
        BmobQuery<Like> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser",user);
        query.include("toDynamic");
        query.findObjects(context, new FindListener<Like>() {
            @Override
            public void onSuccess(List<Like> list) {
                likesList = list;
                if (list.size()>0) {
                    likes.clear();
                    for(Like like:list) {
                        likes.add(like.getToDynamic());
                    }

                }
                adapter.setLikes(likes);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(int i, String s) {
                Log.i("TAG",s+i);
                Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_dynamic_add:
                Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == request_code_Dynamic_details){

            refreshLayout.setRefreshing(true);
            this.data.clear();
            refreshDynamic();
            getLikes();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(context);
    }


}
