package com.rentalphang.runj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.IdentiferUtil;
import com.rentalphang.runj.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static com.rentalphang.runj.utils.IdentiferUtil.IS_NOT_USER;
import static com.rentalphang.runj.utils.IdentiferUtil.IS_User;
import static com.rentalphang.runj.utils.IdentiferUtil.MSG_AUTH_CANCEL;
import static com.rentalphang.runj.utils.IdentiferUtil.MSG_AUTH_COMPLETE;
import static com.rentalphang.runj.utils.IdentiferUtil.MSG_AUTH_ERROR;
import static com.rentalphang.runj.utils.IdentiferUtil.Third_Register_login_Failure;
import static com.rentalphang.runj.utils.IdentiferUtil.Third_Register_login_Success;


/**
 * Created by rentalphang on 2016/11/21.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText etLoginMobile;
    EditText etLoginPassword;
    Button btLogin;
    Button btRegister;
    TextView tvForget;
    ImageView ivwechat;
    ImageView ivweibo;
    ImageView ivqq;
    private String loginNumber;//登录手机号码
    private String loginPassword;//登录密码





    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUTH_CANCEL:
                    //取消授权
                    ToastUtil.setShortToast(context,"取消授权");
                    break;

                case MSG_AUTH_ERROR:
                    //授权失败
                    ToastUtil.setShortToast(context,"取消失败");
                    break;

                case MSG_AUTH_COMPLETE:
                    //授权成功
                    showProgressDialog(LoginActivity.this,"正在登录...");
                    Object[] objs = (Object[]) msg.obj;
                    String platform = (String) objs[0];
                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];

                    //判断用户是否存在
                    isUser(platform);

                    break;
                case IS_NOT_USER://没有用户，注册

                    registerUser((String) msg.obj);

                    break;
                case IS_User://直接登录成功
                    Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    closeProgressDialog();
                    LoginActivity.this.finish();

                    break;
                case Third_Register_login_Success:
                    Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(i);
                    closeProgressDialog();
                    LoginActivity.this.finish();
                    break;
                case Third_Register_login_Failure:
                    closeProgressDialog();
                    ToastUtil.setShortToast(context,"登录失败，请重新尝试");
                    break;
            }



        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();

    }

    /**
     * 初始化组件
     **/

    private void initComponent() {
        etLoginMobile = (EditText) findViewById(R.id.et_login_mobile);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        btLogin = (Button) findViewById(R.id.bt_login);
        btRegister = (Button) findViewById(R.id.bt_login_register);
        tvForget = (TextView) findViewById(R.id.tv_forget_pwd);
        ivwechat = (ImageView) findViewById(R.id.img_wechat_login);
        ivweibo = (ImageView) findViewById(R.id.img_weibo_login);
        ivqq = (ImageView) findViewById(R.id.img_qq_login);

        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
        tvForget.setOnClickListener(this);
        ivqq.setOnClickListener(this);
        ivwechat.setOnClickListener(this);
        ivweibo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.bt_login: // 登录

                if (GeneralUtil.isNetworkAvailable(context) && checkInput()) {
                    String loginSHAPassword = GeneralUtil.getSHAPassword(loginPassword);
                    BmobUser.loginByAccount(context,loginNumber, loginPassword, new LogInListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (user != null) {
                                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (!GeneralUtil.isNetworkAvailable(context)) {
                    Toast.makeText(context, "未连接网络！！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_login_register: // 注册
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.tv_forget_pwd:// 忘记密码

                Intent resetPwdIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetPwdIntent);

                break;
            case R.id.img_wechat_login: // 微信登录
                //微信登录
                //测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
                //打包签名apk,然后才能产生微信的登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);

                break;
            case R.id.img_weibo_login: // 微博登录
                //新浪微博
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(sina);

                break;
            case R.id.img_qq_login: // qq登录
                //qq微博
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
        }

    }


    /***
     * 检查输入
     **/

    private boolean checkInput() {
        loginNumber = etLoginMobile.getText().toString();
        loginPassword = etLoginPassword.getText().toString();

        if (!loginNumber.isEmpty() && !loginNumber.isEmpty()) {
            return true;
        } else if (loginNumber.isEmpty()) {
            ToastUtil.setShortToast(context, "手机号码不能为空");
        } else if (loginPassword.isEmpty()) {
            ToastUtil.setShortToast(context, "密码不能为空");
        }
        return false;
    }

    //执行授权，获取用户信息
    private void authorize(Platform plat) {

        if (plat == null) {
            return;
        }

        if (plat.isAuthValid()) {//已授权

            plat.removeAccount();//清除授权缓存信息
            //            return;
        }

        plat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //授权成功
                if (i == Platform.ACTION_USER_INFOR) {
                    Log.i("TAG", hashMap.toString());

                    String accessToken = platform.getDb().getToken();
                    String openId = platform.getDb().getUserId();
                    Log.i("TAG", "id" + openId);
                    Log.i("TAG", "token" + accessToken);

                    Log.i("TAG", "db" + platform.getDb().exportData());

                    Message msg = new Message();
                    msg.what = MSG_AUTH_COMPLETE;
                    msg.obj = new Object[]{platform.getName(), hashMap};
                    handler.sendMessage(msg);
                }

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                //授权失败
                if (i == Platform.ACTION_USER_INFOR) {
                    handler.sendEmptyMessage(MSG_AUTH_ERROR);
                }
                throwable.printStackTrace();

            }

            @Override
            public void onCancel(Platform platform, int i) {
                //授权取消
                if (i == Platform.ACTION_USER_INFOR) {
                    handler.sendEmptyMessage(MSG_AUTH_CANCEL);
                }

            }
        });
        plat.SSOSetting(false);//优先使用客户端，然后使用网页
        plat.showUser(null);//获取用户资料

    }



    /**
     * 第三方注册用户
     *
     * @param platformName
     */
    private void registerUser(String platformName) {

        Platform platform = ShareSDK.getPlatform(platformName);
        String openId = platform.getDb().getUserId();

        final User user = new User();
        user.setHeadImgUrl(platform.getDb().getUserIcon());
        user.setUsername(openId);
        user.setPassword(openId);
        user.setNickName(platform.getDb().getUserName());

        user.signUp(context,new SaveListener() {


            @Override
            public void onSuccess() {
                user.login(context,new SaveListener() {
                    @Override
                    public void onSuccess() {
                        handler.sendEmptyMessage(Third_Register_login_Success);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        handler.sendEmptyMessage(Third_Register_login_Failure);
                    }

                });
            }

            @Override
            public void onFailure(int i, String s) {
                handler.sendEmptyMessage(Third_Register_login_Failure);
            }
        });

    }

    /**
     * 第三方，判断是否已存在
     *
     * @param platformName
     */
    private void isUser(final String platformName) {

        Platform platform = ShareSDK.getPlatform(platformName);
        String openId = platform.getDb().getUserId();

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", openId);
        query.findObjects(context,new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() == 1) {//存在用户
                    //用户登录
                    User user = list.get(0);
                    user.setPassword(user.getUsername());
                    user.login(context,new SaveListener() {
                        @Override
                        public void onSuccess() {
                            handler.sendEmptyMessage(IdentiferUtil.IS_User);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            handler.sendEmptyMessage(Third_Register_login_Failure);
                        }

                    });
                } else { //不存在用户
                    Message msg = new Message();
                    msg.what = IS_NOT_USER;
                    msg.obj = platformName;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {
                handler.sendEmptyMessage(Third_Register_login_Failure);
            }
        });
    }

//    /**
//     * 获取当前用户
//     * **/
//
//    private void getCurentUser(){
//        BmobUser user = BmobUser.getCurrentUser(context);//获取当前用户
//        if(user != null){
//            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
//            return;
//
//        }
//    }


}
