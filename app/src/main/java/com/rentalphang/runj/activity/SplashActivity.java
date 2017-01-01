package com.rentalphang.runj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.GuiderViewPagerAdapter;
import com.rentalphang.runj.model.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;


/**
 * 启动Activity
 */
public class SplashActivity extends BaseActivity {

    private boolean isFirstSplash = false;//是否是首次启动

    private RelativeLayout firstLayout;
    private RelativeLayout noFirstLayout;
    private LinearLayout indicatorLayout;
    private ViewPager mViewPager;

    private GuiderViewPagerAdapter adapter;

    private List<Integer> views;
    private ImageView[] indicators;//底部指示圆点
    private int currentIndex;//当前选中位置

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();
        getIsFirstSplash();

        //判断是否为首次启动
        if (!isFirstSplash) {

            isFirstSplash = true;
            setIsFirstSplash();

            firstLayout.setVisibility(View.VISIBLE);
            noFirstLayout.setVisibility(View.GONE);

            initViews();
            initIndicator();


        } else{

            firstLayout.setVisibility(View.GONE);
            noFirstLayout.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    judgeAleadyUser();
                }
            },2000);
        }

    }

    /**
     * 判断是否存在用户，已登录过
     */
    private void judgeAleadyUser(){
        if (user == null) {//没有用户
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
        } else {//用户已登录
            Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
        }
        SplashActivity.this.finish();
    }

    /**
     * 初始化圆点
     */
    private void initIndicator(){

        indicators =new ImageView[views.size()];

        for (int i=0;i<views.size();i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.indicator_unselect);
            indicatorLayout.addView(imageView);
        }

        currentIndex = 0;
        ((ImageView)indicatorLayout.getChildAt(0)).setImageResource(R.mipmap.indicator_selector);

    }
    private void initViews(){
        views =new ArrayList<>();
        views.add(R.drawable.default_avatar);
        views.add(R.drawable.default_avatar);
        views.add(R.drawable.default_avatar);
        views.add(R.drawable.default_avatar);
        views.add(R.drawable.default_avatar);
        views.add(R.drawable.default_avatar);

        adapter = new GuiderViewPagerAdapter(context,views);

        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if (position == currentIndex){
                    return;
                }
                ((ImageView)indicatorLayout.getChildAt(currentIndex)).setImageResource(R.mipmap.indicator_unselect);
                ((ImageView)indicatorLayout.getChildAt(position)).setImageResource(R.mipmap.indicator_selector);

                currentIndex = position;

                if (position == 6) {//最后一页

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化组件
     */
    private void initComponent(){
        firstLayout = (RelativeLayout) findViewById(R.id.linear_splash_first);
        noFirstLayout = (RelativeLayout) findViewById(R.id.linear_splash_nofirst);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_user_guider);
        indicatorLayout = (LinearLayout) findViewById(R.id.linear_indicator);
    }

    /**
     * 获取启动标示
     */
    private void getIsFirstSplash(){
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        isFirstSplash = sharedPreferences.getBoolean("isFirstSplash",true);
    }

    /**
     * 存储启动标示
     */
    private void setIsFirstSplash(){
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstSplash",isFirstSplash);
        editor.commit();
        editor.clear();
    }
}
