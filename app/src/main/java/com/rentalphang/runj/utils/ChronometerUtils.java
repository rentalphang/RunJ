package com.rentalphang.runj.utils;

import android.os.Handler;
import android.widget.TextView;



/**
 * Created by rentalphang on 2016/8/11.
 */
public  class ChronometerUtils {


    /**
     * 将秒转化为时分秒
     * **/
    public static String formatTime(int recordingTime){
        String hh=recordingTime/3600>9?recordingTime/3600+"":"0"+recordingTime/3600;
        String  mm=(recordingTime % 3600)/60>9?(recordingTime % 3600)/60+"":"0"+(recordingTime % 3600)/60;
        String ss=(recordingTime % 3600) % 60>9?(recordingTime % 3600) % 60+"":"0"+(recordingTime % 3600) % 60;
        return hh+":"+mm+":"+ss;
    }
    /**
     * 将秒转化为时分秒
     * **/
    public static String formatPeisu(int peiSuSecond){
        String  mm=(peiSuSecond % 3600)/60>9?(peiSuSecond% 3600)/60+"":"0"+(peiSuSecond % 3600)/60;
        String ss=(peiSuSecond % 3600) % 60>9?(peiSuSecond % 3600) % 60+"":"0"+(peiSuSecond% 3600) % 60;
        return mm+"\'"+ss+"\"";
    }



}
