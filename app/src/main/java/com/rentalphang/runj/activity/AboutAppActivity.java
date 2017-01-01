package com.rentalphang.runj.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.rentalphang.runj.R;
import com.rentalphang.runj.model.biz.ActivityManager;


/**
 * 关于APP
 */
public class AboutAppActivity extends BaseActivity {

    private TextView version;//版本
    private TextView link;//官网链接
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ActivityManager.getInstance().pushOneActivity(this);
//        initToolBar();
        version = (TextView) findViewById(R.id.about_app_version);
        version.setText(getVersion());
//        link = (TextView) findViewById(R.id.text_link);
//        link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://runbang.bmob.cn"));
//
//                startActivity(browserIntent);
//
//            }
//        });
    }

//    /**
//     * 初始化toolbar
//     */
//    private void initToolBar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about_app);
//        toolbar.setTitle("关于");
//        toolbar.setNavigationIcon(R.drawable.back);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//    }
    /**
     * 获取版本号
     * @return
     */
    public String getVersion() {
        String versionName = "";
         try {
             PackageManager manager = this.getPackageManager();
             PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
             String version = info.versionName;
             versionName = "V"+version;
         } catch (Exception e) {
             e.printStackTrace();
         }

        return versionName;
    }
}
