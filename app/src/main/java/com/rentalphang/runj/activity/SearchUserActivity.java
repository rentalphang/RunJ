package com.rentalphang.runj.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.SearchUserRecyclerAdapter;
import com.rentalphang.runj.listener.OnRecyclerViewClickListener;
import com.rentalphang.runj.listener.OnRecyclerViewListener;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.UserModel;
import com.rentalphang.runj.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by rentalphang on 2016/12/20.
 */

public class SearchUserActivity extends BaseActivity implements OnRecyclerViewClickListener {

    @Bind(R.id.et_find_name)
    EditText et_find_name;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    @Bind(R.id.btn_search)
    Button btn_search;
    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    SearchUserRecyclerAdapter adapter;
    private List<User> data = new ArrayList<>();
    private User user;

    /**
     * 查询用户成功
     */
    private static final int Query_User_Success = 0x11;



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_User_Success:
                    if (data!=null&&data.size()>0) {

                        adapter.setData(data);
                        adapter.notifyDataSetChanged();
                        sw_refresh.setRefreshing(false);
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        user = BmobUser.getCurrentUser(context,User.class);
        ButterKnife.bind(this);
        initComponent();
    }

    /**
     * 初始化组件
     */
    private void initComponent(){

        adapter =new SearchUserRecyclerAdapter(user,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(linearLayoutManager);
        rc_view.setAdapter(adapter);
        sw_refresh.setEnabled(true);
        sw_refresh.setColorSchemeResources(R.color.colorPrimaryDark);
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });

    }


    @OnClick(R.id.btn_search)
    public void onSearchClick(View view){
        sw_refresh.setRefreshing(true);
        query();
    }
    @OnClick(R.id.iv_search_back)
    public void onSearchBackClick(View view){
        finish();
    }




    public void query() {
        String name = et_find_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.setShortToast(context,"请填写用户名");
            sw_refresh.setRefreshing(false);
            return;
        }
        BmobQuery<User> query = new BmobQuery<>();
        //去掉当前用户
        try {
            String userNickName = (String) BmobUser.getObjectByKey(context,"nickName");
            query.addWhereNotEqualTo("nickName",userNickName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.addWhereContains("nickName", name);
        query.setLimit(10);
        query.order("-createdAt");
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    data.clear();
                    data = list;
                    Message msg = new Message();
                    msg.what = Query_User_Success;
                    handler.sendMessage(msg);
                } else {
                    ToastUtil.setShortToast(context,"查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("TAG",i + s);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(SearchUserActivity.this,PersonProfileActivity.class);
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
            case R.id.bt_search_chat:
                if (data.get(position).getObjectId() != user.getObjectId()) {
                    BmobIMUserInfo info = new BmobIMUserInfo(
                            data.get(position).getObjectId(), data.get(position).getNickName(), data.get(position).getHeadImgUrl());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(getPackageName(), bundle);
                    startActivity(intent);
                }
        }

    }
}
