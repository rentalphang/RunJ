package com.rentalphang.runj.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by rentalphang on 2016/11/17.
 */

public class ToastUtil {

    public static void setShortToast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

    public static void setLongToast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }
}
