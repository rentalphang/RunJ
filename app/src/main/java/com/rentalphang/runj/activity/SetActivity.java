package com.rentalphang.runj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rentalphang.runj.R;
import com.rentalphang.runj.model.biz.ActivityManager;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

import static com.baidu.location.h.j.S;

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout modifyInfo;
    private RelativeLayout bindAccount;
    private RelativeLayout notification;
    private RelativeLayout updateVersion;
    private RelativeLayout feedback;
    private RelativeLayout clearCache;
    private RelativeLayout about;
    private RelativeLayout signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();

    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        //        notification = (RelativeLayout) findViewById(R.id.set_notification_relative);
        updateVersion = (RelativeLayout) findViewById(R.id.set_update_version_relative);
        feedback = (RelativeLayout) findViewById(R.id.set_feedback_relative);
        clearCache = (RelativeLayout) findViewById(R.id.set_clear_cache_relative);
        about = (RelativeLayout) findViewById(R.id.set_about_relative);
        signOut = (RelativeLayout) findViewById(R.id.set_sign_out_relative);

        //        notification.setOnClickListener(this);
        updateVersion.setOnClickListener(this);
        feedback.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        about.setOnClickListener(this);
        signOut.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //            case R.id.set_notification_relative:
            //                break;
            case R.id.set_update_version_relative://检查版本更新


                showProgressDialog(SetActivity.this, "检查中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        BmobUpdateAgent.forceUpdate(context);
                        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                            @Override
                            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                                if (i == UpdateStatus.Yes) {//版本有更新

                                    closeProgressDialog();

                                } else if (i == UpdateStatus.No) {
                                    closeProgressDialog();

                                    Toast.makeText(SetActivity.this, "版本无更新", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }, 3000);

                break;
            case R.id.set_feedback_relative:
                Toast.makeText(SetActivity.this, "暂无此功能，敬请期待！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_clear_cache_relative:

                showProgressDialog(SetActivity.this, "清理中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ImageLoader.getInstance().clearMemoryCache();
                        ImageLoader.getInstance().clearDiskCache();
                        closeProgressDialog();
                        Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);

                break;
            case R.id.set_about_relative:

                Intent aboutIntent = new Intent(this, AboutAppActivity.class);
                startActivity(aboutIntent);

                break;
            case R.id.set_sign_out_relative:

                new AlertDialog.Builder(SetActivity.this)
                        .setMessage("确定退出登录吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BmobUser.logOut(context);//清除缓存用户对象
                                ActivityManager.getInstance().popAllActivity();//退出应用
                                Intent loginIntent = new Intent(SetActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                SetActivity.this.finish();
                            }
                        }).create().show();
                break;
        }
    }


}
