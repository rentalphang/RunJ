package com.rentalphang.runj.model.biz;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类，单例类
 * 管理所有的Activity
 * Created by rentalphang on 2016/11/21.
 */


public class ActivityManager {

    private Stack<Activity> activityStack;//activity管理栈
    private static ActivityManager activityManager;

    private ActivityManager() {
        activityStack = new Stack<>();
    }


    /**
     * 获取ActivityStack的实例
     **/

    public static ActivityManager getInstance() {
        if (activityManager == null) {
            activityManager = new ActivityManager();
        }
        return activityManager;
    }


    /**
     * 弹出栈顶Activity
     **/

    public void popTopActivity() {
        if (activityStack != null) {
            Activity activity = activityStack.peek();
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 移除一个Activity
     **/
    public void removeOneActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            activityStack.remove(activity);
            activity = null;
        }
    }


    /**
     * 将一个Activity压入栈
     * **/

    public void pushOneActivity(Activity activity){
        if(activityStack == null){
            activityStack = new Stack<>();
            activityStack.add(activity);
        }else {
            activityStack.add(activity);
        }
    }

    /**
     * 退出时移除所有Activity
     * **/

    public void popAllActivity(){
        if(activityStack != null){
            while(activityStack.size() >0){
                Activity activity = activityStack.peek();
                activityStack.remove(activity);
                if(activity != null){
                    activity.finish();
                }
                activityStack.remove(activity);

            }
        }
    }


}





