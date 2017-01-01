package com.rentalphang.runj.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.ThreeTwoOneActivity;
import com.rentalphang.runj.map.LocationMap;
import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.GeneralUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by rentalphang on 2016/7/29.
 */
public class RunFragment extends Fragment implements View.OnClickListener, RadarSearchListener {
    private View mRootView;
    LocationMap locationMap;
    RadarSearchManager mManager;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    public MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    boolean isFirstLoc = true; // 是否首次定位


    TextureMapView mMapView = null;
    BaiduMap mBaiduMap;
    private Button btStartRun;
    private TextView tvPeopleNum;
    private TextView tvTotalDistance;
    private TextView tvTotalCount;


    private LatLng latLng;

    private User user;

    private double totalDistance = 0.0; //总距离


    private int totalCount = 0; //次数


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = BmobUser.getCurrentUser(getActivity(),User.class);
        queryData();

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_run, null);
            initMap(mRootView, getActivity().getApplicationContext());
            initComponent();
            initRandar();
            nearBySearch();


        }
        ViewGroup parents = (ViewGroup) mRootView.getParent();
        if (parents != null) {
            parents.removeView(mRootView);

        }
        return mRootView;
    }


    private void initComponent() {
        btStartRun = (Button) mRootView.findViewById(R.id.btn_startRun);
        tvPeopleNum = (TextView) mRootView.findViewById(R.id.tv_home_people_number);
        tvTotalDistance = (TextView) mRootView.findViewById(R.id.tv_home_total_distance);
        tvTotalCount = (TextView) mRootView.findViewById(R.id.tv_home_total_count);

        btStartRun.setOnClickListener(this);
    }


    public void initMap(View mRootView, Context ctx) {

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        //设置定位图标
        mCurrentMarker = null;


        // 地图初始化
        mMapView = (TextureMapView) mRootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(ctx);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

    }


    private void initRandar() {

        //周边雷达功能模块进行初始化
        mManager = RadarSearchManager.getInstance();
        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);
        String nickName = (String) BmobUser.getObjectByKey(getActivity(), "nickName");
        //周边雷达设置用户身份标识，id为空默认是设备标识
        mManager.setUserID(user.getObjectId());

    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            double latitude = location.getLatitude(); //纬度
            double longitude = location.getLongitude(); // 经度
            int gpsAccuracyStatus = location.getGpsAccuracyStatus();//GPS信号强度

            latLng = new LatLng(latitude, longitude); //坐标点

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 设置控件数据
     * **/
    private void setViewData(){

        tvTotalDistance.setText(GeneralUtil.doubleToString(totalDistance));
        tvTotalCount.setText(String.valueOf(totalCount));

    }




    /**
     * 查询运动数据
     */
    private void queryData() {
        BmobQuery<RunRecord> query = new BmobQuery<>();
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = query.hasCachedResult(getActivity(),RunRecord.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        query.addWhereEqualTo("userId", user.getObjectId());
        query.setLimit(50);
        query.findObjects(getActivity(), new FindListener<RunRecord>() {
            @Override
            public void onSuccess(List<RunRecord> list) {
                if (list.size() > 0) {
                    totalDistance = 0.0;
                    for (RunRecord runRecord : list) {
                        totalDistance += runRecord.getDistance();
                    }
                    totalCount = list.size();
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        setViewData();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startRun:

                //得到系统的位置服务，判断GPS是否激活
                LocationManager lm=(LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
                boolean ok=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (ok) {
                    Intent intent = new Intent(mRootView.getContext(), ThreeTwoOneActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_exit, R.anim.anim_enter);
                }else {
                    new com.rentalphang.runj.ui.AlertDialog(getActivity(), "GPS未开启", "为了正常记录你的运动数据，嗨跑需要你开启GPS定位功能。", true, "开启",0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
                        @Override
                        public void onDialogButtonClick(int requestCode, boolean isPositive) {
                            if (isPositive) {
                                Intent intent=new Intent();
                                intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }

                        }
                    }).show();
                }



                break;

        }

    }

    private void nearBySearch() {

        //构造请求参数，其中centerPt是自己的位置坐标
        RadarNearbySearchOption option = new RadarNearbySearchOption().centerPt(latLng).pageNum(0).pageCapacity(50).radius(2000);
        //发起查询请求
        mManager.nearbyInfoRequest(option);
    }

    public void onMapDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onMapDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

}
