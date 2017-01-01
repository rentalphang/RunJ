package com.rentalphang.runj.utils;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.List;

/**
 *
 * Created by 洋 on 2016/5/4.
 */
public class JsonUtil {

//    /**
//     * 解析json,获取城市列表
//     * @param json
//     * @return
//     */
//    public static CityList parseCityListJson(String json){
//        CityList cityList = new CityList();
//        Gson gson = new Gson();
//        cityList = gson.fromJson(json,CityList.class);
//
//        return cityList;
//    }
//
//    /**
//     *解析json,获取天气信息
//     * @param json
//     * @return
//     */
//    public static WeatherData parseWeatherJson(String json) {
//        WeatherList weatherList = new WeatherList();
//        Gson gson = new Gson();
//        weatherList = gson.fromJson(json,WeatherList.class);
//
//        return weatherList.getWeatherDatas().get(0);
//
//    }


    /**
     * list集合转化为json
     */
    public static String listTojson(List list) {

        String json = null;
        Gson gson = new Gson();
        json = gson.toJson(list);
        return json;
    }

    /**
     * json转化为list,获取坐标点集合
     * @param json
     * @return
     */
    public static List<LatLng> jsonToListPoint(String json) {

        List<LatLng> list = null;
        Gson gson = new Gson();

        list = gson.fromJson(json, new TypeToken<List<LatLng>>() {
        }.getType());

        return list;
    }

    /**
     * json转化为list,获取速度集合
     * @param json
     * @return
     */
    public static List<Float> jsonToListSpeed(String json){

        List<Float> list = null;
        Gson gson = new Gson();

        list = gson.fromJson(json,new TypeToken<List<Float>>(){}.getType());

        return list;

    }

//    /**
//     * json转化为list,获取离线城市集合
//     * @param json
//     * @return
//     */
//    public static List<OLCity> jsonToListOfflineCity(String json) {
//        List<OLCity> list = null;
//
//        Gson gson = new Gson();
//        list = gson.fromJson(json,new TypeToken<List<OLCity>>(){}.getType());
//
//        return list;
//    }
//
//    /**
//     * 解析JSON，获取朋友关系集合
//     * @param json
//     * @return
//     */
//    public static List<Friend> parseFriendJson(String json) {
//        List<Friend> friends = null;
//
//        Gson gson = new Gson();
//
//        friends = gson.fromJson(json,new TypeToken<List<Friend>>(){}.getType());
//
//        return friends;
//    }

}
