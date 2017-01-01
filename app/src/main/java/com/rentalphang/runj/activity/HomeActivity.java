package com.rentalphang.runj.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import com.baidu.mapapi.SDKInitializer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.fragment.CommunityFragment;
import com.rentalphang.runj.fragment.DiscoverFragment;
import com.rentalphang.runj.fragment.MeFragment;
import com.rentalphang.runj.fragment.MessageFragment;
import com.rentalphang.runj.fragment.RunFragment;
import com.rentalphang.runj.model.bean.User;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;


/**
 * Created by rentalphang on 2016/7/29.
 */
public class HomeActivity extends FragmentActivity{

    private BmobUser user ;

    private FragmentTabHost mTabHost;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {CommunityFragment.class,DiscoverFragment.class,RunFragment.class,MessageFragment.class,
            MeFragment.class};
    private Fragment mFragment[] = {new CommunityFragment(),new DiscoverFragment(),new RunFragment(),new MessageFragment()
            ,new MeFragment(),
            };
    private String mTitles[] = {"社区","发现","跑步","消息","我"};
    private int mImages[] = {
            R.drawable.tab_community_icon,
            R.drawable.tab_discover_icon,
            R.drawable.tab_run_icon,
            R.drawable.tab_message_icon,
            R.drawable.tab_me_icon
    };
    private int mLayout[] = {
            R.layout.tab_item_community,
            R.layout.tab_item_discover,
            R.layout.tab_item_run,
            R.layout.tab_item_message,
            R.layout.tab_item_me};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
        user = BmobUser.getCurrentUser(this,User.class);//获取当前用户
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);

        if (user!= null) {
            setIMConnect();
        }
        setUpdate();


    }

    private void init() {
        initView();
        mTabHost.setCurrentTab(2); //设置跑步为默认页

    }

    private void setIMConnect(){
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("TAG", "连接成功");
                } else {
                    Log.i("TAG", s + e);
                }
            }
        });

        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus connectionStatus) {

                if (connectionStatus == ConnectionStatus.DISCONNECT) { //断开连接
                    //重新连接
                    setIMConnect();
                } else if (connectionStatus == ConnectionStatus.NETWORK_UNAVAILABLE) { //网络问题

                }
            }
        });
    }

     /**
     * 初始化导航栏
     * **/
    private void initView() {
        //实例化TABHOST对象
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentList = new ArrayList<Fragment>();
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //使TAB之间分割线消失
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0;i < mFragment.length;i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec,mClass[i],null);
            mFragmentList.add(mFragment[i]);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
        }


    }

    /**
     * 检查版本更新
     */
    private void setUpdate(){
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                //根据i来判断更新是否成功
            }
        });
        BmobUpdateAgent.setUpdateCheckConfig(false);
    }

    /**
     * 获取一个TAB标签
     * **/
    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(mLayout[index], null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);
        return view;
    }

    /**
     * 获取当前用户
     * **/

    private void getCurrentUser(){
        if(user!= null){
            return;
        }else{//要是没有用户缓存，跳转到登录界面
            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }




}
