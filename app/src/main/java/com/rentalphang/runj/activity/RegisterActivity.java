package com.rentalphang.runj.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
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


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;




/**
 * Created by rentalphang on 2016/11/17.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    private EditText etReigisterMobile;
    private EditText etRigisterCode;
    private EditText etRigisterPassword;
    private Button btGetCode;
    private Button btRegisterSubmit;
    private CheckBox cbEyeOpen;
    private String mobileNumber = null;//手机号码
    private String verifyCode = null;//验证码
    private String registerPassword = null;//密码
    private MyCountTimer timer;



    Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IdentiferUtil.Is_Mobile_unHave://手机号未注册，为新用户
                    timer = new MyCountTimer(60000, 1000);
                    timer.start();
                    btGetCode.setEnabled(false);
                    getVerifyCode();
                    break;

                case IdentiferUtil.Is_Mobile_Have://手机号码已被注册
                    etReigisterMobile.setText("");
                    ToastUtil.setShortToast(context, "该手机号码已被注册，请重新输入");
                    break;

                case IdentiferUtil.VERIFY_CODE_SUCCESS://手机验证成功
                    Intent intent = new Intent(RegisterActivity.this, RegisterWHeightActivity.class);
                    intent.putExtra("mobileNumber", mobileNumber);
//                    intent.putExtra("password", GeneralUtil.getSHAPassword(registerPassword));//对密码进行SHA-256加密
                    intent.putExtra("password", registerPassword);
                    startActivity(intent);
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BmobSMS.initialize(context, ConfigUtil.BMOB_APP_ID);
        setContentView(R.layout.activity_register);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();
        setListener();
    }


    /***
     * 初始化组件
     **/
    private void initComponent() {
        etReigisterMobile = (EditText) findViewById(R.id.et_register_mobile);
        etRigisterCode = (EditText) findViewById(R.id.et_register_verify_code);
        etRigisterPassword = (EditText) findViewById(R.id.et_register_password);
        btGetCode = (Button) findViewById(R.id.bt_getverifycode);
        btRegisterSubmit = (Button) findViewById(R.id.bt_register_submit);
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
        btRegisterSubmit.setOnClickListener(this);
        showPwd();
    }


    /**
     * 设置输入监听器
     ****/
    private void setListener() {
        etReigisterMobile.addTextChangedListener(new TextWatcher() {//手机号码输入
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = etReigisterMobile.getText().toString();

            }
        });

        etRigisterCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyCode = etRigisterCode.getText().toString();
            }
        });

        etRigisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                registerPassword = etRigisterPassword.getText().toString();
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
            case R.id.bt_getverifycode://获取验证码
                if (checkInput()) {
                    //判断手机号码是否已注册
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("userName", mobileNumber);
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

            case R.id.bt_register_submit://注册
                if (checkInput()) {//检查输入是否正确
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtil.setShortToast(context, "验证码不能为空");
                    } else if (GeneralUtil.isPasswordNumber(registerPassword)) {
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
                    etRigisterPassword.setTransformationMethod(null);
                } else {
                    etRigisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etRigisterPassword.setSelection(etRigisterPassword.length());
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
