package com.rentalphang.runj.utils;

/**
 * 基本配置
 * Created by 洋 on 2016/4/22.
 */
public class ConfigUtil {

    /**
     * Application ID ，初始化用到密钥
     */
    public static final String BMOB_APP_ID = "033db1894e6262436e0a8a1b2005305d";

    /**
     * REST API Key , REST API请求中HTTP头部信息必须附带密钥之一
     */
    public static final String BMOB_API_KEY = "6baccc89d2206e60d584eacdbea672ee";

    /**
     * Secret key ，是SDK安全密钥，不可泄漏，在云端逻辑测试云端代码时需要用到
     */
    public static final String BMOB_SECRET_KEY = "d72cb97ebafadbf6";

    /**
     * Master Key , 超级权限Key。应用开发或调试的时候可以使用该密钥进行各种权限的操作，此密钥不可泄漏
     */
    public static final String BMOB_MASTER_KEY = "59a4cabcc76da57739a552d429120a12";

    /**
     * apikey ，APIStore通用key
     */
    public  static final String APISTORE_API_KEY = "618aa2b9fcfa0575e8acefb8c843f76a";


    /**
     * 鹰眼服务id
     */
    public static final String YINGYAN_SERVICE_ID = "115788";

    /**
     * 天气接口地址
     */
    public static final String WEATHER_API = "http://apis.baidu.com/heweather/weather/free";

    /**
     * 城市列表接口
     */
    public static final String CITY_LIST_API = " https://api.heweather.com/x3/citylist?search=allchina" +
            "&key=a7ec86e719d9458da2e1f67ebc73d2e4";

}
