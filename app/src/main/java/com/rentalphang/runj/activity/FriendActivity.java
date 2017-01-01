package com.rentalphang.runj.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.SearchUserRecyclerAdapter;
import com.rentalphang.runj.listener.OnRecyclerViewClickListener;
import com.rentalphang.runj.model.bean.Friend;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.ActivityManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class FriendActivity extends BaseActivity implements View.OnClickListener {


    private static final int Get_Fans_Success = 0x11;

    private static final int Get_User_Follow_Success = 0x12;

    private static final int Get_Follow_Success = 0x15;

    private static final int Follow_User_Success = 0x13;

    private static final int Follow_user_fail = 0x14;

    private ImageView backImg;
    private TextView titleText;
    private RecyclerView recyclerView;

    private boolean sign;//true为粉丝，false为关注
    private String userid = null ;
    private String username = null;

    private SearchUserRecyclerAdapter adapter;

    private List<User> data = new ArrayList<>();

    private List<User> followData = new ArrayList<>(); //用户关注列表

    private User queryUser;
    private User user;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Get_Follow_Success:
                    Log.i("TAG","粉丝粉丝");
                    if (sign) {//粉丝
                        Log.i("TAG","粉丝粉丝");
                        getFans();

                    } else {//关注
                        if(user.equals(queryUser)) {
                            data = followData;
                            adapter.setFollowData(followData);
                            adapter.setData(data);
                            adapter.notifyDataSetChanged();
                        } else {

                            queryUserFollow();
                        }
                    }
                    break;
                case Get_User_Follow_Success:
                    adapter.setFollowData(followData);
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                    break;
                case Get_Fans_Success:

                    adapter.setFollowData(followData);
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                    break;

                case Follow_user_fail:
                    new AlertDialog.Builder(FriendActivity.this)
                            .setMessage("关注失败，请稍后重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                    break;
               case Follow_User_Success:

                    new AlertDialog.Builder(FriendActivity.this)
                            .setMessage("关注成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ActivityManager.getInstance().pushOneActivity(this);

        user = BmobUser.getCurrentUser(context,User.class);

        userid = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        sign = getIntent().getBooleanExtra("sign", false);
        queryUser = new User();
        queryUser.setObjectId(userid);
        queryUser.setUsername(username);

        initComponent();

        if (sign) { //粉丝
            titleText.setText(username+"的粉丝");
        } else {//关注
            titleText.setText(username+"的关注");
        }
        //获取关注列表
        getFollow();

    }

    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.friend_back_img);
        titleText = (TextView) findViewById(R.id.friend_title_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_friend);

        backImg.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchUserRecyclerAdapter(user, new OnRecyclerViewClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(FriendActivity.this,PersonProfileActivity.class);
                intent.putExtra("userId",data.get(position).getObjectId());
                startActivity(intent);

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

            @Override
            public void onChildClick(int position, int childId) {

                switch (childId){
                    case R.id.linear_item_add_friend_follow://关注

                        User aimUser = data.get(position);

                        if (aimUser!=user && !followData.contains(aimUser)) {

                            Friend friend = new Friend();
                            friend.setFromUser(user);
                            friend.setToUser(aimUser);

                            friend.save(context, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    handler.sendEmptyMessage(Follow_User_Success);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    handler.sendEmptyMessage(Follow_user_fail);
                                }
                            });
                        }

                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    /**
     * 获取关注用户集合
     */
    private void getFollow(){

        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", user);
        query.include("toUser");
        query.addQueryKeys("toUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() > 0) {
                    followData.clear();
                    for (Friend friend : list) {
                        followData.add(friend.getToUser());
                    }
                }
                handler.sendEmptyMessage(Get_Follow_Success);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 获取关注用户集合
     */
    private void queryUserFollow(){

        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", queryUser);
        query.include("toUser");
        query.addQueryKeys("toUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size()>0) {
                    data.clear();
                    for (Friend friend:list) {
                        data.add(friend.getToUser());
                    }
                }
                handler.sendEmptyMessage(Get_User_Follow_Success);
            }
            @Override
            public void onError(int i, String s) {

            }
        });

    }
    /**
     * 获取粉丝用户的集合
     */
    private void getFans(){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", queryUser);
        query.include("fromUser");
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size()>0) {
                    Log.i("TAG",list.size()+"daxaa");
                    data.clear();
                    for (Friend friend:list) {
                        data.add(friend.getFromUser());
                    }
                    handler.sendEmptyMessage(Get_Fans_Success);
                }
            }
            @Override
            public void onError(int i, String s) {

                Log.i("TAG",s+i);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()) {
            case R.id.friend_back_img:
                this.finish();
                break;
        }
    }
}
