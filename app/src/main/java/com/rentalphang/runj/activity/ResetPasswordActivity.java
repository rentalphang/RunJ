package com.rentalphang.runj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.ActivityManager;
import com.rentalphang.runj.utils.ConfigUtil;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.IdentiferUtil;
import com.rentalphang.runj.utils.ToastUtil;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;


/**
 * Created by rentalphang on 2016/11/17.
 */

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {


    private EditText etResetMobile;
    private EditText etResetCode;
    private EditText etResetPassword;
    private Button btGetCode;
    private Button btResetSubmit;
    private CheckBox cbEyeOpen;
    private String mobileNumber = null;//手机号码
    private String verifyCode = null;//验证码
    private String resetPassword = null;//密码
    private MyCountTimer timer;
    private boolean isVisible = true;//密码隐藏与显示


    Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IdentiferUtil.Is_Mobile_unHave://手机号未注册，为新用户
                    etResetMobile.setText("");
                    ToastUtil.setShortToast(context, "该手机号码未注册，请前往注册页面进行注册");
                    break;

                case IdentiferUtil.Is_Mobile_Have://手机号码已被注册

                    timer = new MyCountTimer(60000, 1000);
                    timer.start();
                    btGetCode.setEnabled(false);
                    getVerifyCode();
                    break;

                case IdentiferUtil.VERIFY_CODE_SUCCESS://手机验证成功
                    Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                    intent.putExtra("mobileNumber", mobileNumber);
                    intent.putExtra("password", GeneralUtil.getSHAPassword(resetPassword));//对密码进行SHA-256加密
                    startActivity(intent);
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BmobSMS.initialize(context, ConfigUtil.BMOB_APP_ID);
        setContentView(R.layout.activity_reset_password);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();
        setListener();
    }


    /***
     * 初始化组件
     **/
    private void initComponent() {
        etResetMobile = (EditText) findViewById(R.id.et_reset_mobile);
        etResetCode = (EditText) findViewById(R.id.et_reset_verify_code);
        etResetPassword = (EditText) findViewById(R.id.et_reset_password);
        btGetCode = (Button) findViewById(R.id.bt_reset_getverifycode);
        btResetSubmit = (Button) findViewById(R.id.bt_resetpassword_submit);
        cbEyeOpen = (CheckBox) findViewById(R.id.cb_eyeopen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btGetCode.setOnClickListener(this);
        btResetSubmit.setOnClickListener(this);
        showPwd();
    }


    /**
     * 设置输入监听器
     ****/
    private void setListener() {
        etResetMobile.addTextChangedListener(new TextWatcher() {//手机号码输入
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = etResetMobile.getText().toString();

            }
        });

        etResetCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyCode = etResetCode.getText().toString();
            }
        });

        etResetPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                resetPassword = etResetPassword.getText().toString();
            }
        });


    }

    /***
     * 检测输入
     **/

    private boolean checkInput() {
        if (GeneralUtil.isNetworkAvailable(context)) {
            if (TextUtils.isEmpty(mobileNumber)) {
                ToastUtil.setShortToast(context, "手机号码不能为空");
            } else if (GeneralUtil.isMobileNumber(mobileNumber)) {
                return true;
            } else {
                ToastUtil.setShortToast(context, "手机号码格式不正确");
            }
        } else {
            ToastUtil.setShortToast(context, "未连接网络!");
        }

        return false;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_reset_getverifycode://获取验证码
                if (checkInput()) {
                    //判断手机号码是否已注册
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("username", mobileNumber);
                    query.count(context,User.class, new CountListener() {
                        @Override
                        public void onSuccess(int i) {
                            if (i <= 0) {//不存在
                                Message msg = new Message();
                                msg.what = IdentiferUtil.Is_Mobile_unHave;
                                handler.sendMessage(msg);
                            } else {//已经存在
                                Message msg = new Message();
                                msg.what = IdentiferUtil.Is_Mobile_Have;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                }
                break;

            case R.id.bt_resetpassword_submit://提交重设密码
                if (checkInput()) {//检查输入是否正确
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtil.setShortToast(context, "验证码不能为空");
                    } else if (GeneralUtil.isPasswordNumber(resetPassword)) {
                        verifyCode(verifyCode);//验证验证码
                    } else {
                        ToastUtil.setShortToast(context, "密码位数输入有误，请重新输入");
                    }

                }
                break;


        }

    }

    /***
     * 请求验证码
     **/
    private void getVerifyCode() {
        BmobSMS.requestSMSCode(context, mobileNumber, "验证码", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if (e == null) {//验证码发送成功
                    Message msg = new Message();
                    msg.what = IdentiferUtil.GET_CODE_SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    timer.cancel();
                    btGetCode.setText("发送验证码");
                    btGetCode.setEnabled(true);
                    ToastUtil.setShortToast(context, "获取验证码失败");

                }
            }

        });
    }


    /***
     * 验证验证码
     **/

    private void verifyCode(String code) {
        BmobSMS.verifySmsCode(context, mobileNumber, code, new VerifySMSCodeListener() {
            @Override
            public void done(cn.bmob.sms.exception.BmobException e) {
                if (e == null) {//短信验证成功
                    Message msg = new Message();
                    msg.what = IdentiferUtil.VERIFY_CODE_SUCCESS;
                    handler.sendMessage(msg);

                } else {
                    Message msg = new Message();
                    msg.what = IdentiferUtil.VERIFY_CODE_FAILURE;
                    handler.sendMessage(msg);
                }
            }
        });
    }




    /***
     * 计时器
     */

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btGetCode.setText((millisUntilFinished / 1000) + "S后重发");

        }

        @Override
        public void onFinish() {
            btGetCode.setText("重新发送");
            btGetCode.setEnabled(true);

        }
    }


    /**
     * 输入框显示密码
     **/

    private void showPwd() {

        cbEyeOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("TAG", "checked");
                if (isChecked) {
                    etResetPassword.setTransformationMethod(null);
                } else {
                    etResetPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etResetPassword.setSelection(etResetPassword.length());
            }
        });

    }




    //
    //    /**
    //     * 自动填充短信验证码
    //     * **/
    //    class MySMSCodeListener implements SMSCodeListener {
    //
    //        @Override
    //        public void onReceive(String content) {
    //            if(etRigisterCode != null){
    //                etRigisterCode.setText(content);
    //            }
    //        }
    //
    //    }


}
