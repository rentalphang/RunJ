package com.rentalphang.runj.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 通用工具类
 *
 * Created by 洋 on 2016/4/24.
 */
public class GeneralUtil {


    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";

    /**
     * 检验是否为手机号码
     * @param number
     * @return
     */
    public static boolean isMobileNumber(String number){

        String regex="^((13[0-9]|(15[^4,\\D])|(17[0,6,7])|(18[0-9]))\\d{8}$)";

        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(number);

        return matcher.matches();

    }

    /**
     *
     * 是否为Email地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email){

        String regex="^([a-zA-Z0-9_-])+@(([a-zA-Z0-9_-]+)[.])+[a-z]{2,4}$";
        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(email);

        return matcher.matches();

    }

    /**
     * 是否为纯数字
     * @param number
     * @return
     */
    public static boolean isNumber(String number){
        String regex="^[0-9]*$";

        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(number);

        return matcher.matches();

    }

    /**
     * 正则表达式检验
     * @param input
     * @param regex
     * @return
     */
    public static boolean regularExpressions(String input , String regex ){
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(input);
        return  matcher.matches();
    }

    /**
     * 密码位数是否正确
     * @param password
     * @return
     */
    public static boolean isPasswordNumber(String password){
        if(password.length()>=6 && password.length()<=20){
            return true;
        }
        return false;
    }


    /**
     * 网络是否可连接
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){

        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 判断是否存在SD卡
     * @return
     */
    public static boolean isSDCard(){

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 字符串转化为Date
     * @param str
     * @return
     */
    public static Date stringToDate(String str){
        Date date=null;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        try {
            date=simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 创建时间转换为Date
     * @param str
     * @return
     */
    public static Date createdAtToDate(String str){
        Date date=null;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            date=simpleDateFormat.parse(str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 总秒数转换为00:00:00 格式字符串
     * @param time
     * @return
     */
    public static  String secondsToString(int time) {

        int house = 0; //小时
        int minute = 0; // 分钟
        int second = 0; //秒

        String houseStr ="00";
        String minuteStr ="00";
        String secondStr = "00";

        second = time % 60;
        minute = (time / 60) % 60;
        house = (time /60) / 60;
        if (second <10) {
            secondStr = "0"+second;
        } else {
            secondStr = Integer.toString(second);
        }

        if ( minute < 10) {
            minuteStr = "0"+minute;
        } else {
            minuteStr = Integer.toString(minute);
        }

        if (house < 10) {
            houseStr = "0"+house;

        } else {
            houseStr = Integer.toString(house);
        }

        return houseStr+":"+minuteStr+":"+secondStr;

    }

    /**
     * 秒数转化为小时
     * @param time
     * @return
     */
    public static String secondsToHourString(int time){

        String timeStr = "0.0";

        double hour = time/3600.0;

        double h = Math.round(hour*10)/10.0;

        if(h>0){
            timeStr = h+"";
        }

        return timeStr;


    }

    /**
     * 距离(米）转化为字符串 0.00格式 公里
     * @param distance
     * @return
     */
    public static String doubleToString(double distance) {
        String distanceStr = "0.00";

        //转化为千米（公里）
        double km = distance/1000;

        // 四舍五入，保留2为小数
        double d= Math.round(km*100)/100.0;


        //转化为字符串
        if(d > 0) {
            distanceStr = String.valueOf(d);
        }


        return distanceStr;
    }

    public static String distanceToKm(double distance) {

        return String.valueOf(distance/1000);
    }

    /**
     * 空气质量指数AQI ，根据AQI数值返回空气质量状况
     * @param aqi
     * @return
     */
    public static String valueToAQIState( String aqi) {

        String state = null;
        int value = Integer.parseInt(aqi);

        if( value <= 50) {
            state = "优";
        } else if (value <= 100) {
            state = "良";
        } else if (value <= 150) {
            state = "轻度污染";
        } else if (value <= 200) {
            state = "中度污染";
        } else if (value <= 300) {
            state = "重度污染";
        } else if (value >300) {
            state = "严重污染";
        }
        return state;

    }


    /**
     * 判断是否打开GPS
     * @param context
     * @return
     */
    public static boolean isOpenGPS(Context context){

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean agps = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(gps || agps) {
            return true;
        }
        return false;
    }


    /**
     * 离线地图数据包大小格式化
     * @param size
     * @return
     */
    public static String formatDataSize(int size) {

        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }


    /**
     * 计算时间差
     * @param date
     * @return
     */
    public static String computeTime(String date){
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date createDate = null;
        try {
            createDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = nowDate.getTime() - createDate.getTime();

        Log.i("TAG",time+"时间");
        if ( (time/1000)/60 <=0 ) { //不到1分钟
            int second = Math.round(time/1000);
            return second+"秒前";
        } else if ( (time/1000)/3600 <=0){ //不到一小时

            int minute = Math.round(time/1000/60);

            return minute+"分钟前";

        } else if ( (time/1000)/3600/24 <=0) { //不到一天

            int house = Math.round(time/1000/3600);

            return house +"小时前";

        } else if ( (time/1000)/3600/24/7 <=0) { //不到一周

            int day = Math.round(time/1000/3600/24);

            return day +"周前";
        } else {

            return date;
        }

    }

    /**
     * 获取日期
     * @param str
     * @return
     */
    public static  String getDayFromDate(String str) {
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int day = 0;
        try {
            date =sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(day);

    }

    /**
     * 获取月份
     * @param str
     * @return
     */
    public static String getMonthFromDate(String str) {
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int month = 0;
        try {
            date =sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            month = calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(month)+"月";
    }

    /**
     * 判断是否为同一天
     * @param str1
     * @param str2
     * @return
     */
    public static  boolean isSameDate(String str1,String str2) {
        Date date1,date2;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isMonth = false,isDay = false,isYear = false;
        try {
            date1 = sdf.parse(str1);
            date2 = sdf.parse(str2);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date2);

            isDay = calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
            isMonth = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
            isYear = calendar1.get(Calendar.YEAR)==calendar2.get(Calendar.YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isYear&&isMonth&&isDay;
    }

    /**
     *根据生日计算年龄
     * @param date
     * @return
     */
    public static String getAgeByBirthday(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birthday ;
        Date nowDate = new Date();

        int age = 0;

        try {
            birthday = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(nowDate);
            //计算年龄
            age = calendar1.get(Calendar.YEAR)-calendar.get(Calendar.YEAR)+1;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(age);
    }

    public static String getChatTime(boolean hasYear,long timesamp) {
        long clearTime = timesamp;
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(clearTime);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(clearTime);
                break;
            case 2:
                result = "前天 " + getHourAndMin(clearTime);
                break;
            default:
                result = getTime(hasYear,clearTime);
                break;
        }
        return result;
    }
    public static String getTime(boolean hasYear,long time) {
        String pattern=FORMAT_DATE_TIME;
        if(!hasYear){
            pattern = FORMAT_MONTH_DAY_TIME;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(time));
    }
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_TIME);
        return format.format(new Date(time));
    }

    /**
     * 对密码进行SHA-256加密
     *
     * **/
    public static String getSHAPassword(String registerPassword){
        try {
            byte[] input = registerPassword.getBytes("GBK");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input);
            byte[] ouput = sha.digest();
            String result = Base64.encodeToString(ouput, Base64.DEFAULT);
            return result;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
