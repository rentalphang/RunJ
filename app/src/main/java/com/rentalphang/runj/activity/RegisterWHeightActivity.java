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
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.password;
import static com.baidu.location.h.j.o;

/**
 * Created by rentalphang on 2016/11/25.
 */

public class RegisterWHeightActivity extends BaseActivity implements View.OnClickListener {

    private final static int REQUEST_CODE_CAMERA=0X001; //拍照的requestcode

    private final static int REQUEST_CODE_ALBUM = 0x002; // 从相册中选择图片的requestCode

    private final static int UPLOAD_SUCCESS = 0x11; //上传图片成功

    private final static int UPLOAD_FAILURE = 0x12; //上传图片失败

    private final static int REGISTER_SUCCESS = 0x21; // 注册成功

    private final static int REGISTER_FAILURE = 0X22; // 注册失败

    private final static int Create_Timeline_Success = 0x31; //创建时间线成功

    private final static int Create_Timeline_Failure = 0x32; //创建时间线失败



    @Bind(R.id.scaleWheelView_height)
    ScaleRulerView mHeightWheelView;
    @Bind(R.id.tv_user_height_value)
    TextView mHeightValue;


    @Bind(R.id.ruler_weight)
    DecimalScaleRulerView mWeightRulerView;
    @Bind(R.id.tv_user_weight_value_two)
    TextView mWeightValueTwo;

    @Bind(R.id.bt_register_finish)
    Button btFinish;

    @Bind(R.id.tv_register_cancel)
    TextView tvCancel;

    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;


    private float mWeight = 60.0f;
    private float mMaxWeight = 200;
    private float mMinWeight = 25;

    private String mobileNumber=null;
    private String password=null;


    private User user; //用户


    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case UPLOAD_SUCCESS:
                    //注册
                    registerUser();
                    break;

                case UPLOAD_FAILURE:
                    closeProgressDialog();
                    ToastUtil.setShortToast(context,"上传头像失败");
                    registerUser();
                    break;

                case REGISTER_SUCCESS:
                    closeProgressDialog();
                    showSuccessDialog();
                    break;
                case REGISTER_FAILURE:
                    closeProgressDialog();
                    showFailureDialog();
                    break;

                case Create_Timeline_Failure: //创建时间线失败
                    Log.i("TAG","创建时间线失败");
                    closeProgressDialog();
                    showSuccessDialog();
                    break;

            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerwheight);
        ButterKnife.bind(this);  //依赖注入
        init();
        ActivityManager.getInstance().pushOneActivity(this);
        mobileNumber = getIntent().getStringExtra("mobileNumber");//获得手机号码
        password = getIntent().getStringExtra("password");//获得密码
        btFinish.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }




    private void init() {
        mHeightValue.setText((int) mHeight + "");
        mWeightValueTwo.setText(mWeight + "kg");


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
     * 注册用户
     */
    private void registerUser(){

        user=new User();
        user.setUsername(mobileNumber);
        user.setNickName(mobileNumber);
        user.setPassword(password);
        user.setMobilePhoneNumber(mobileNumber);
        user.setHeight(mHeight);
        user.setWeight(mWeight);
        //注册
        user.signUp(context,new SaveListener() {
            @Override
            public void onSuccess() {
                createTimeline();
            }

            @Override
            public void onFailure(int i, String s) {
                Message msg = new Message();
                msg.what = REGISTER_FAILURE;
                handler.sendMessage(msg);
            }
        });

    }

    /**
     * 创建新用户时间线
     */
    private void createTimeline(){
        Timeline timeline = new Timeline();
        timeline.setFromUser(user);
        timeline.save(context,new SaveListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what = REGISTER_SUCCESS;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {
                Message msg = new Message();
                msg.what = Create_Timeline_Failure;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register_finish:
                showProgressDialog(this,"正在注册...");
                registerUser();
                break;

            case R.id.tv_register_cancel:
                showCancelDialog();
                break;

        }

    }

    /**
     * 显示dialog
     * **/
    private void showSuccessDialog(){

        new com.rentalphang.runj.ui.AlertDialog(this, "注意", "注册成功，请登录!", false, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if(isPositive){
                    Intent intent = new Intent(RegisterWHeightActivity.this,LoginActivity.class);
                    startActivity(intent);
                    RegisterWHeightActivity.this.finish();
                }

            }
        }).show();

    }
    /**
     * 显示失败dialog
     * **/
    private void showFailureDialog(){

        new com.rentalphang.runj.ui.AlertDialog(this, "注册失败", "是否重试？", true,0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if(isPositive){
                    return;
                }else{
                    finish();
                }

            }
        }).show();

    }
    /**
     * 显示取消注册dialog
     * **/
    private void showCancelDialog(){

        new com.rentalphang.runj.ui.AlertDialog(this, "取消注册", "确定取消账户注册？嗨跑需要你！", true,0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if(isPositive){
                    finish();
                }else{
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
