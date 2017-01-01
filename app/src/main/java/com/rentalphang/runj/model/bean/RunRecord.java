package com.rentalphang.runj.model.bean;


import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by rentalphang on 2016/11/4.
 */

public class RunRecord extends BmobObject {

    private String recordId; //记录ID
    private String userId; //用户ID

    private int time; //用时

    private double distance; //距离
    private double avgSpeed;//平均速度
    private String peiSu;//配速



    private int kcal;//卡路里
    private List<LatLng> points; //坐标点集合
    private String createTime;    //创建时间

    private boolean isSync; //同步标示，true 已同步，false 未同步






    public  int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public String getPeiSu() {
        return peiSu;
    }

    public void setPeiSu(String peiSu) {
        this.peiSu = peiSu;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
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


    @Override
    public boolean equals(Object o) {
        if (o instanceof RunRecord) {
            RunRecord record = (RunRecord)o;

            return this.recordId.equals(record.getRecordId());

        }
        return super.equals(o);
    }
}
