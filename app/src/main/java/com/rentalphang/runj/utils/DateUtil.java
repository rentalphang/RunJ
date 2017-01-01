package com.rentalphang.runj.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private DateUtil(){
        throw new AssertionError();
    }

    // 把日期转为字符串
    public static String converToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    // 把字符串转为日期
    public static Date converToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(strDate);
    }

    public static String converToStringWithFormatter(Date date, String formatter){
        DateFormat df = new SimpleDateFormat(formatter);
        return df.format(date);
    }

}
