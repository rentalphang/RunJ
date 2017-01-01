package com.rentalphang.runj.utils;

/**
 *
 * 标识类
 *
 * Created by 洋 on 2016/5/1.
 */
public class IdentiferUtil {

    /**
     * 下载文件成功
     */
    public static final int DOWN_FILE_SUCCESS = 0X01;

    /**
     * 下载文件失败
     */
    public static final int DOWN_FILE_FAIL = 0x02;

    /**
     * 查询单个数据成功
     */
    public static final int QUERY_SINGLE_DATA_SUCCESS = 0x03;

    /**
     * 计时任务
     */
    public static final int TIME_TASK = 0x04;

    /**
     * 获取天气数据成功
     */
    public static final int GET_WEATHER_SUCCESS = 0X05;

    /**
     * 删除一条runRecord成功
     */
    public  static final int DELETE_RUN_RECORD_SUCCESS = 0X06;

    /**
     * 删除一条runRecord失败
     */
    public  static final int DELETE_RUN_RECORD_FAILURE = 0X07;
    /**
     * 保存数据成功
     */
    public static final int SAVE_DATA_TO_BMOB_SUCCESS = 0X08;
    /**
     * 保存数据失败
     */
    public static final int SAVE_DATA_TO_BMOB_FAILURE = 0X09;

    /**
     * 开始记录时间
     * */
    public static final int START_TO_RECORD_TIME = 0X10;

    /**
     * 开始刷新路程等信息
     * **/
    public static final int REFRESH_UI = 0x11;

    /***
     * 注册获取验证码成功
     * */
    public static final int GET_CODE_SUCCESS = 0x100;

    /**
     * 注册获取验证码失败
     * */
    public static final int GET_CODE_FAILURE = 0x101;


    /**
     * 验证验证码成功
     * **/
    public static final int VERIFY_CODE_SUCCESS = 0x200;


    /**
     * 验证验证码失败
     * ***/
    public static final int VERIFY_CODE_FAILURE = 0x201;

    /**
     * 手机号存在
     * **/
    public static final int Is_Mobile_Have = 0x31;

    /***
     *手机号码不存在
     * **/
    public static final int Is_Mobile_unHave = 0x32;


    /**
     *
     * */
    public static final int MSG_SMSSDK_CALLBACK = 1;
    /**
     * 授权取消
     * */
    public static final int MSG_AUTH_CANCEL = 2;
    /**
     * 授权失败
     * */
    public static final int MSG_AUTH_ERROR= 3;

    /**
     * 授权成功
     * */
    public static final int MSG_AUTH_COMPLETE = 4;

    /**
     * 存在用户
     * **/
    public static final int IS_User = 5;

    /***
     * 不存在用户
     * **/
    public static final int IS_NOT_USER = 6;

    /**
     * 第三方登录成功
     * ***/
    public static final int Third_Register_login_Success = 7;

    /**
     * 第三方登录失败
     * **/
    public static final int Third_Register_login_Failure = 8;



}
