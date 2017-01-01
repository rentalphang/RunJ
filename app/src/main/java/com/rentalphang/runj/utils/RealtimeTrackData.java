package com.rentalphang.runj.utils;

import java.util.List;

import com.baidu.mapapi.model.LatLng;

/**
 * 实时轨迹数据
 * 
 * 
 */
public class RealtimeTrackData {
    public int status; // 状态码，0为成功
    public int size; // 返回结果条数，该页返回了几条数据
    public int total; // 符合条件结果条数，一共有几条符合条件的数据
    public List<Entities> entities;
    public String message; // 响应信息,对status的中文描述

    public class Entities {
        public String create_time; // 创建时间 格式化时间 该时间为服务端时间
        public String modify_time; // 修改时间
        public RealtimePoint realtime_point; // 实时轨迹信息

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getModify_time() {
            return modify_time;
        }

        public void setModify_time(String modify_time) {
            this.modify_time = modify_time;
        }

        public RealtimePoint getRealtime_point() {
            return realtime_point;
        }

        public void setRealtime_point(RealtimePoint realtime_point) {
            this.realtime_point = realtime_point;
        }

    }

    public class RealtimePoint {
        public List<Double> location;// 经纬度 Array 百度加密坐标
        public String loc_time;// 该track实时点的上传时间 UNIX时间戳 该时间为用户上传的时间

        public List<Double> getLocation() {
            return location;
        }

        public void setLocation(List<Double> location) {
            this.location = location;
        }

        public String getLoc_time() {
            return loc_time;
        }

        public void setLoc_time(String loc_time) {
            this.loc_time = loc_time;
        }

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Entities> getEntities() {
        return entities;
    }

    public void setEntities(List<Entities> entities) {
        this.entities = entities;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LatLng getRealtimePoint() {

        if (entities.get(0).realtime_point == null) {
            return null;
        }

        List<Double> location = entities.get(0).realtime_point.location;
        if (Math.abs(location.get(0) - 0.0) < 0.01 && Math.abs(location.get(1) - 0.0) < 0.01) {
            return null;
        } else {
            return new LatLng(location.get(1), location.get(0));
        }
    }
}
