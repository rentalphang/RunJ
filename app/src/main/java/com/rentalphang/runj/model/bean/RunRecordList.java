package com.rentalphang.runj.model.bean;

/**
 * Created by rentalphang on 2016/12/5.
 */

public class RunRecordList {

    private String createTime;    //创建时间
    private double distance; //距离
    private int time; //用时
    private boolean isSync; //同步标示，true 已同步，false 未同步

    public RunRecordList(String createTime, int time, double distance, boolean isSync) {
        this.createTime = createTime;
        this.time = time;
        this.distance = distance;
        this.isSync = isSync;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }


}
