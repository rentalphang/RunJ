package com.rentalphang.runj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.Timeline;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.ActivityManager;
import com.rentalphang.runj.ui.DecimalScaleRulerView;
import com.rentalphang.runj.ui.ScaleRulerView;
import com.rentalphang.runj.utils.DrawUtil;
import com.rentalphang.runj.utils.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.baidu.location.h.j.M;

/**
 * Created by rentalphang on 2016/11/25.
 */

public class ModifyWHeightActivity extends BaseActivity implements View.OnClickListener {



    private final static int MODIFY_SUCCESS = 0x21; // 修改成功

    private final static int MODIFY_FAILURE = 0X22; // 修改失败




    @Bind(R.id.scaleWheelView_height)
    ScaleRulerView mHeightWheelView;
    @Bind(R.id.tv_user_height_value)
    TextView mHeightValue;


    @Bind(R.id.ruler_weight)
    DecimalScaleRulerView mWeightRulerView;
    @Bind(R.id.tv_user_weight_value_two)
    TextView mWeightValueTwo;

    @Bind(R.id.bt_modify_wh_finish)
    Button btWHFinish;

    @Bind(R.id.tv_modify_wh_cancel)
    TextView tvWHCancel;

    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;


    private float mWeight = 60.0f;
    private float mMaxWeight = 200;
    private float mMinWeight = 25;

    private String mobileNumber = null;
    private String password = null;


    private User user; //用户


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MODIFY_SUCCESS:
                    closeProgressDialog();
                    showSuccessDialog();
                    break;
                case MODIFY_FAILURE:
                    closeProgressDialog();
                    showFailureDialog();
                    break;


            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyrwheight);
        ButterKnife.bind(this);  //依赖注入
        init();
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context,User.class);
        btWHFinish.setOnClickListener(this);
        tvWHCancel.setOnClickListener(this);
    }


    private void init() {
        mHeightValue.setText((int) user.getHeight() + "");
        mWeightValueTwo.setText(user.getWeight() + "kg");


        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = value;
            }
        });


        mWeightRulerView.setParam(DrawUtil.dip2px(10), DrawUtil.dip2px(32), DrawUtil.dip2px(24),
                DrawUtil.dip2px(14), DrawUtil.dip2px(9), DrawUtil.dip2px(12));
        mWeightRulerView.initViewParam(mWeight, mMinWeight, mMaxWeight, 1);
        mWeightRulerView.setValueChangeListener(new DecimalScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValueTwo.setText(value + "kg");

                mWeight = value;
            }
        });
    }


    /**
     * 提交修改
     */
    public void updateInfo() {
        User xUser = new User();
        xUser.setWeight(mWeight);
        xUser.setHeight(mHeight);
        xUser.update(this,user.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(MODIFY_SUCCESS);
            }

            @Override
            public void onFailure(int i, String s) {
                handler.sendEmptyMessage(MODIFY_FAILURE);

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_modify_wh_finish:
                showProgressDialog(this, "正在修改中...");
                updateInfo();
                break;

            case R.id.tv_modify_wh_cancel:
                showCancelDialog();
                break;

        }

    }

    /**
     * 显示dialog
     **/
    private void showSuccessDialog() {

        new com.rentalphang.runj.ui.AlertDialog(this, "注意", "修改成功！", false, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive) {
                    ModifyWHeightActivity.this.finish();
                }

            }
        }).show();

    }

    /**
     * 显示失败dialog
     **/
    private void showFailureDialog() {

        new com.rentalphang.runj.ui.AlertDialog(this, "注册失败", "是否重试？", true, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive) {
                    return;
                } else {
                    finish();
                }

            }
        }).show();

    }

    /**
     * 显示取消注册dialog
     **/
    private void showCancelDialog() {

        new com.rentalphang.runj.ui.AlertDialog(this, "取消注册", "确定取消账户注册？嗨跑需要你！", true, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive) {
                    finish();
                } else {
                    return;
                }

            }
        }).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
