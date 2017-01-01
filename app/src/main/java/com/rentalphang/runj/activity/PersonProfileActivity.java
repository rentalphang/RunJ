package com.rentalphang.runj.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.PersonalDynamicAdapter;
import com.rentalphang.runj.listener.OnRecyclerViewListener;
import com.rentalphang.runj.model.bean.Dynamic;
import com.rentalphang.runj.model.bean.Friend;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.ToastUtil;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import static android.R.attr.data;
import static android.R.id.list;
import static com.baidu.location.h.j.s;


public class PersonProfileActivity extends BaseActivity implements View.OnClickListener {


    private static final int Query_Dynamic_Success = 0x11;
    /**
     * 关注用户失败
     */
    private static final int Delete_Follow_User_Success = 0x12;
    /**
     * 关注用户成功
     */
    private static final int Delete_Follow_user_Fail = 0x13;
    /**
     * 取消关注用户失败
     */
    private static final int Follow_user_Fail = 0x14;
    /**
     * 取消关注用户成功
     */
    private static final int Follow_User_Success = 0x15;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ImageView ivAvatar;
    private ImageView ivFollow;
    private TextView tvNickName;
    private TextView tvFansNumber;
    private TextView tvFollowNumber;
    private TextView tvBtFollow;
    private LinearLayout llFansLinear;
    private LinearLayout llFollowLinear;
    private RelativeLayout rlChat;
    private RelativeLayout rlFollow;

    private List<Dynamic> dynamicData = null; //动态数据集合
    private PersonalDynamicAdapter adapter;

    private DisplayImageOptions circleOptions;

    private User user;//当前用户

    private User queryUser;//查询用户

    private String userid;//查询用户id

    private List<User> followList = new ArrayList<>();//当前用户关注对象集合
    private List<String> fansIDList = new ArrayList<>();//当前用户粉丝ID集合
    private String curFriendId;//当前用户关注查询用户的Friend的objectId

    private int followCount = 0;//关注人数
    private int fansCount = 0;//粉丝人数
    private boolean isFollow ;//当前用户是否关注

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_Dynamic_Success:
                    setupRecyclerView(recyclerView);
                    break;

                case Follow_User_Success:
                    isFollow = true;
                    tvBtFollow.setText("取消关注");
                    ivFollow.setImageDrawable(getResources().getDrawable(R.mipmap.icon_cancel_follow));
                    rlFollow.setBackgroundColor(getResources().getColor(R.color.gray));
                    ToastUtil.setShortToast(context,"关注成功");
                    break;

                case Follow_user_Fail:
                    ToastUtil.setShortToast(context,"关注失败，请稍后重试");
                    break;
                case Delete_Follow_User_Success:
                    tvBtFollow.setText("关注");
                    ivFollow.setImageDrawable(getResources().getDrawable(R.mipmap.icon_add));
                    rlFollow.setBackgroundColor(getResources().getColor(R.color.yellow));
                    ToastUtil.setShortToast(context,"取消关注成功");
                    break;

                case Delete_Follow_user_Fail:
                    ToastUtil.setShortToast(context,"取消关注失败，请稍后重试");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        ButterKnife.bind(this);
        user = BmobUser.getCurrentUser(context, User.class);
        userid = getIntent().getStringExtra("userId");
        initComponent();
        queryFollowList();
        if (userid.equals(user.getObjectId())) {//是当前用户
            rlChat.setVisibility(View.GONE);
            rlFollow.setVisibility(View.GONE);
            queryUser = user;

            //绑定数据
            bindDataToView();

            queryFansNumber();

            queryFollowNumber();

            queryPersonDynamic();


        } else {
            rlChat.setVisibility(View.VISIBLE);
            rlFollow.setVisibility(View.VISIBLE);
            queryUser();
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PersonalDynamicAdapter(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {

                Intent dynamicIntent = new Intent(PersonProfileActivity.this, DynamicDetailsActivity.class);
                dynamicIntent.putExtra("dynamicId", dynamicData.get(position).getObjectId());
                startActivity(dynamicIntent);
                PersonProfileActivity.this.finish();
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

            @Override
            public void onChildClick(int position, int childId) {

            }
        });
        adapter.setData(dynamicData);

        recyclerView.setAdapter(adapter);
    }

    private void initComponent() {

        //初始化toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_personal_profile);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setTitle("个人简介");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_personal);
//        collapsingToolbarLayout.setTitle("个人简介");

        //初始化recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_personal_dynamic);

        ivAvatar = (ImageView) findViewById(R.id.iv_person_avatar);
        ivFollow = (ImageView) findViewById(R.id.iv_follow);
        tvNickName = (TextView) findViewById(R.id.tv_person_nickname);
        tvFansNumber = (TextView) findViewById(R.id.tv_fans);
        tvFollowNumber = (TextView) findViewById(R.id.tv_follow);
        tvBtFollow = (TextView) findViewById(R.id.tv_bt_follow);
        llFansLinear = (LinearLayout) findViewById(R.id.ll_fans);
        llFollowLinear = (LinearLayout) findViewById(R.id.ll_follow);
        rlChat = (RelativeLayout) findViewById(R.id.relative_chat);
        rlFollow = (RelativeLayout) findViewById(R.id.rl_follow);

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        rlChat.setOnClickListener(this);
        rlFollow.setOnClickListener(this);
        llFollowLinear.setOnClickListener(this);
        llFansLinear.setOnClickListener(this);
    }


    private void bindDataToView() {

        ImageLoader.getInstance().displayImage(queryUser.getHeadImgUrl(), ivAvatar, circleOptions);
        tvNickName.setText(queryUser.getNickName());
        tvFansNumber.setText(fansCount + "");
        tvFollowNumber.setText(followCount + "");


    }

    /**
     * 通过objectId查询用户
     */
    private void queryUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(context, userid, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                queryUser = user;


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bindDataToView();
                    }
                });


                queryFansNumber();

                queryFollowNumber();

                queryPersonDynamic();

                queryFansList();//查询粉丝



                if (fansIDList.contains(user.getObjectId())) {
                    isFollow = true;
                    tvBtFollow.setText("取消关注");
                    ivFollow.setImageDrawable(getResources().getDrawable(R.mipmap.icon_cancel_follow));
                    rlFollow.setBackgroundColor(getResources().getColor(R.color.gray));

                }



            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", i + s);
            }
        });
    }

    /**
     * 查询个人动态
     */
    private void queryPersonDynamic() {
        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", queryUser);
        query.order("-createdAt");
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
//        boolean isCache = query.hasCachedResult(context, Dynamic.class);
//        if (isCache) {
//            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//        } else {
//            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//        }
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {

                dynamicData = list;
                Message msg = new Message();
                msg.what = Query_Dynamic_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {

                Log.i("TAG", s + i);
            }
        });

    }

    /**
     * 查询关注
     */
    private void queryFollowNumber() {
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", queryUser);
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                followCount = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvFollowNumber.setText(followCount + "");
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }


    /**
     * 查询当前用户关注集合
     */
    private void queryFollowList() {
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", user);
        query.include("toUser");
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() > 0) {
                    for (Friend friend : list) {
                        followList.add(friend.getToUser());
                    }
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 查询当前用户是否关注
     */
    private void queryFansList() {
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", queryUser);
        query.include("fromUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() > 0) {
                    for (Friend friend : list) {
                        fansIDList.add(friend.getFromUser().getObjectId());
                        if(friend.getFromUser().getObjectId() == user.getObjectId()){
                            isFollow = true;
                        }
                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                ToastUtil.setShortToast(context,s);

            }
        });
    }

    /**
     * 查询粉丝
     */
    private void queryFansNumber() {
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", queryUser);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                fansCount = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        tvFansNumber.setText(fansCount + "");
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //        getMenuInflater().inflate(R.menu.menu_personal_profile, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.relative_chat:

                if (!queryUser.equals(user)) {
                    BmobIMUserInfo info = new BmobIMUserInfo(
                            queryUser.getObjectId(), queryUser.getNickName(), queryUser.getHeadImgUrl());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(getPackageName(), bundle);
                    startActivity(intent);
                }

                break;
            case R.id.ll_fans:

                if (fansCount > 0) {
                    Intent fansIntent = new Intent(this, FriendActivity.class);
                    fansIntent.putExtra("userId", queryUser.getObjectId());
                    fansIntent.putExtra("username", queryUser.getNickName());
                    fansIntent.putExtra("sign", true);
                    startActivity(fansIntent);
                }
                break;
            case R.id.ll_follow:
                if (followCount > 0) {
                    Intent followIntent = new Intent(this, FriendActivity.class);
                    followIntent.putExtra("userId", queryUser.getObjectId());
                    followIntent.putExtra("sign", false);
                    followIntent.putExtra("username", queryUser.getNickName());
                    startActivity(followIntent);
                }
                break;

            case R.id.rl_follow:
                if(isFollow){//已经关注该用户
                    Friend friend = new Friend();
                    friend.setFromUser(user);
                    friend.setToUser(queryUser);
                    friend.delete(context, new DeleteListener(){
                        @Override
                        public void onSuccess() {
                            Message msg = new Message();
                            msg.what = Delete_Follow_User_Success;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ToastUtil.setShortToast(context,s);
                            handler.sendEmptyMessage(Delete_Follow_user_Fail);
                        }
                    });
                } else{//没有关注该用户
                    if(userid != user.getObjectId() && !fansIDList.contains(user.getObjectId())){
                        Friend friend = new Friend();
                        friend.setFromUser(user);
                        friend.setToUser(queryUser);
                        friend.save(context, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                Message msg = new Message();
                                msg.what = Follow_User_Success;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                                handler.sendEmptyMessage(Follow_user_Fail);
                            }
                        });
                    }

                }

                break;
        }
    }
}
