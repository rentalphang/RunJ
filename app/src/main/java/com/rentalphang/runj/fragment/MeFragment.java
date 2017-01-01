package com.rentalphang.runj.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.ModifyWHeightActivity;
import com.rentalphang.runj.activity.PersonProfileActivity;
import com.rentalphang.runj.activity.RegisterActivity;
import com.rentalphang.runj.activity.RegisterUserActivity;
import com.rentalphang.runj.activity.RunRecordActivity;
import com.rentalphang.runj.activity.SetActivity;
import com.rentalphang.runj.db.DBManager;
import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.GeneralUtil;
import com.xinlan.discview.DiscView;

import java.util.List;

import cn.bmob.v3.BmobUser;

import static com.baidu.location.h.j.G;


/**
 * Created by rentalphang on 2016/7/29.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private DiscView mDiscView;
    private ImageView ivMeIcon;
    private TextView tvName;
    private TextView tvTips;
    private Button btSetting;
    private RelativeLayout rlRunRecord;
    private RelativeLayout rlAlterInfo;
    private RelativeLayout rlAlterWeiHeightInfo;
    private User user;
    private DisplayImageOptions circleOptions;
    private double totalDistance;//总里程
    private int totalTarget;//总目标



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mRootView = inflater.inflate(R.layout.fragment_me,null);
            initComponent();

        user = BmobUser.getCurrentUser(getActivity(),User.class);
        updateInfo();
        updateRunInfo();//更新跑步信息
        mDiscView.setValue((int)totalDistance,100);//设置环形
        ViewGroup parents = (ViewGroup) mRootView.getParent();
        if(parents != null){
            parents.removeView(mRootView);
        }
        return mRootView;
    }

    private void initComponent(){
        mDiscView = (DiscView)mRootView.findViewById(R.id.disc_view);
        ivMeIcon = (ImageView) mRootView.findViewById(R.id.iv_me_icon);
        tvName = (TextView) mRootView.findViewById(R.id.tv_name);
        tvTips = (TextView) mRootView.findViewById(R.id.tv_me_tips);
        btSetting = (Button) mRootView.findViewById(R.id.bt_me_setting);
        rlRunRecord = (RelativeLayout) mRootView.findViewById(R.id.rl_runrecord);
        rlAlterInfo = (RelativeLayout) mRootView.findViewById(R.id.rl_alter_info);
        rlAlterWeiHeightInfo = (RelativeLayout) mRootView.findViewById(R.id.rl_alter_weheight_info);


        mDiscView.setOnClickListener(this);
        ivMeIcon.setOnClickListener(this);
        btSetting.setOnClickListener(this);
        rlRunRecord.setOnClickListener(this);
        rlAlterInfo.setOnClickListener(this);
        rlAlterWeiHeightInfo.setOnClickListener(this);


        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();


    }

    /**
     * 更新账号信息
     * */

    private void updateInfo(){
        tvName.setText(user.getNickName());//更新用户名
        if(user.getHeadImgPath() !=null){//更新头像
            ImageLoader.getInstance().displayImage(user.getHeadImgUrl(),ivMeIcon,circleOptions);
        }else{
            ImageLoader.getInstance().displayImage(user.getHeadImgUrl(),ivMeIcon,circleOptions);
        }
        tvTips.setText("你离第一个100KM，还有"+ (100-(int)totalDistance) +"KM，继续加油吧！");
    }


    private void updateRunInfo(){
        List<RunRecord> data = DBManager.getInstance(getActivity()).getRunRecords();
        for (RunRecord runRecord:data){
            totalDistance += runRecord.getDistance();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_me_setting://设置界面
                Intent settingIntent = new Intent(getActivity(), SetActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.rl_runrecord://跑步记录
                Intent runRecordIntent = new Intent(getActivity(), RunRecordActivity.class);
                startActivity(runRecordIntent);
                break;

            case R.id.rl_alter_info://修改信息
                Intent registerIntent = new Intent(getActivity(), RegisterUserActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.rl_alter_weheight_info://修改信息
                Intent modifyWHIntent = new Intent(getActivity(), ModifyWHeightActivity.class);
                startActivity(modifyWHIntent);
                break;

            case R.id.iv_me_icon://头像（个人界面）
                Intent personIntent = new Intent(getActivity(), PersonProfileActivity.class);
                personIntent.putExtra("userId",user.getObjectId());
                startActivity(personIntent);
                break;
        }


    }
}
