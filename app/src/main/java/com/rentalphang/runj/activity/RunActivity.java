package com.rentalphang.runj.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.utils.DistanceUtil;
import com.rentalphang.runj.R;
import com.rentalphang.runj.db.DBManager;
import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.ChronometerUtils;
import com.rentalphang.runj.utils.FileUtil;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.IdentiferUtil;
import com.rentalphang.runj.utils.ToastUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


import static com.baidu.location.BDLocation.GPS_ACCURACY_BAD;
import static com.baidu.location.BDLocation.GPS_ACCURACY_GOOD;
import static com.baidu.location.BDLocation.GPS_ACCURACY_MID;
import static com.baidu.location.BDLocation.GPS_ACCURACY_UNKNOWN;
import static vi.com.gdi.bgl.android.java.EnvDrawText.pt;


/**
 * Created by rentalphang on 2016/10/11.
 */

public class RunActivity extends BaseActivity implements View.OnClickListener, RadarUploadInfoCallback {

    private Button btnTracePause;
    private Button btnTraceStop;
    private Button btnTraceShare;
    private Button btnTraceContinue;
    private TextView tvDistance;
    private TextView tvSpeed;
    private TextView tvPeisu;
    private TextView tvTime;
    private TextView tvKcal;
    private TextView tvTraceState;

    private TextView dialogMessage;
    private TextView dialogMContinue;
    private TextView dialogEnd;
    private ImageView ivGpsState;
    private TextView tvGpsTips;
    private View itemData;



    private User user;

    private Dialog dialog; // 弹窗提示

    //    private User user;
    //
    //    private RunRecord runRecord = null ; // 跑步记录

    private String picPath = null; // 截屏路径

    private boolean isStart = false; // 标示 是否开始运动，默认false，未开始

    private double distance = 0.00; // 跑步总距离

    private String peiSu = "0\'0\""; //配速

    private double avgSpeed = 0.00;//平均速度

    private int time = 0; //跑步用时，单位秒

    private static final int REFRESH_TIME = 4000;   //4秒刷新一次

    private LatLng latLng;

    private String userNickName;

    private int userWeight;

    private String snapShotPath = null;

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    /**
     * 定位监听器
     */
    private BDLocationListener locationListener = new MyLocationListener();

    private List<LatLng> pointList = new ArrayList<>(); //坐标点集合

    private BaiduMap baiduMap;  //地图对象

    private MapView mapView;

    private RunRecord runRecord;

    private RadarSearchManager mManager;


    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;

    /**
     * 地图状态更新
     */
    private MapStatusUpdate update = null;

    /**
     * 实时点覆盖物
     */
    private OverlayOptions realtimeOptions = null;

    /**
     * 开始点覆盖物
     */
    private OverlayOptions startOptions = null;

    /**
     * 结束点覆盖物
     */
    private OverlayOptions endOptions = null;

    /**
     * 路径覆盖物
     */
    private PolylineOptions polyLine = null;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IdentiferUtil.START_TO_RECORD_TIME:
                    time++;
                    tvTime.setText(ChronometerUtils.formatTime(time));
                    return;

                case IdentiferUtil.SAVE_DATA_TO_BMOB_SUCCESS:
                    runRecord.setSync(true);
                    DBManager.getInstance(context).insertRunRecord(runRecord);
                    return;

                case IdentiferUtil.SAVE_DATA_TO_BMOB_FAILURE:
                    runRecord.setSync(false);
                    DBManager.getInstance(context).insertRunRecord(runRecord);
                    return;


            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_run);
        initComponent();
        user = User.getCurrentUser(context,User.class);
        isStart = true;
        baiduMap = mapView.getMap();
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(locationListener);
        initLocation();
        initListener();
        initCurrentUserInfo();
        mLocationClient.start();
        new Thread(new TimeThread()).start();//开始计时
        initRadar();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_tracePause:
                isStart = false;
                btnTracePause.setVisibility(View.INVISIBLE);
                btnTraceContinue.setVisibility(View.VISIBLE);
                btnTraceStop.setVisibility(View.VISIBLE);
                tvTraceState.setText("跑步暂停中");

                break;

            case R.id.bt_traceStop:
                isStart = false;
                showDialog();
                tvTraceState.setText("完成跑步");
                break;

            case R.id.bt_traveContinue:
                isStart = true;
                new Thread(new RunActivity.TimeThread()).start();
                btnTracePause.setVisibility(View.VISIBLE);
                btnTraceContinue.setVisibility(View.INVISIBLE);
                btnTraceStop.setVisibility(View.INVISIBLE);
                tvTraceState.setText("跑步进行中");
                break;

            case R.id.dialog_continue_run:
                isStart = true;
                dialog.dismiss();
                break;

            case R.id.bt_traceShare:
                mapScreenShot();//截图

                ShareSDK.initSDK(context);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // title标题：微信、QQ（新浪微博不需要标题）
                oks.setTitle("今天，你跑了吗？");  //最多30个字符
                // text是分享文本：所有平台都需要这个字段
                oks.setText(runRecord.getCreateTime() + "我用嗨跑跑了" + GeneralUtil.distanceToKm(runRecord.getDistance()) + "千米"
                        + ",花费了" + GeneralUtil.secondsToHourString(runRecord.getTime()) + "小时");  //最多40个字符

                // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
                if (snapShotPath != null) {
                    oks.setImagePath(snapShotPath);//确保SDcard下面存在此张图片
                }

                //                oks.setImageUrl(dynamic1.getImage().get(0));//网络图片rul

                //                // url：仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://www.qq.com");   //网友点进链接后，可以看到分享的详情
                //                // Url：仅在QQ空间使用
                oks.setTitleUrl("http://www.qq.com");  //网友点进链接后，可以看到分享的详情
                // 启动分享GUI
                oks.show(context);


                break;



        }

    }


    /**
     * 计时线程
     */
    private class TimeThread implements Runnable {

        @Override
        public void run() {

            try {
                while (isStart) {
                    Message msg = Message.obtain();
                    msg.what = IdentiferUtil.START_TO_RECORD_TIME;
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 初始化组件
     */
    private void initComponent() {

        mapView = (MapView) findViewById(R.id.traceMapView);
        baiduMap = mapView.getMap();
        mapView.showZoomControls(false);

        btnTracePause = (Button) findViewById(R.id.bt_tracePause);
        btnTraceShare = (Button) findViewById(R.id.bt_traceShare);
        btnTraceStop = (Button) findViewById(R.id.bt_traceStop);
        btnTraceContinue = (Button) findViewById(R.id.bt_traveContinue);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTraceState = (TextView) findViewById(R.id.tv_traceStatus);
        tvSpeed = (TextView) findViewById(R.id.tv_speed);
        tvPeisu = (TextView) findViewById(R.id.tv_peisu);
        tvKcal = (TextView) findViewById(R.id.tv_kcal);
        ivGpsState = (ImageView) findViewById(R.id.iv_gps_state);
        tvGpsTips = (TextView) findViewById(R.id.tv_gps_tips);
        itemData = findViewById(R.id.item_detaildata_run);
        btnTraceContinue.setOnClickListener(this);
        btnTraceStop.setOnClickListener(this);
        btnTracePause.setOnClickListener(this);
        btnTraceShare.setOnClickListener(this);
    }

    /**
     * 绘制轨迹
     *
     * @param latLng
     */
    private void drawTrace(LatLng latLng) {

        Log.i("TAG", "绘制实时点");

        baiduMap.clear(); //清除覆盖物

        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();

        update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //实时点
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);


        realtimeOptions = new MarkerOptions().position(latLng).icon(realtimeBitmap)
                .zIndex(9).draggable(true);

        if (isStart) {
            // 开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start_detail);

            if (pointList.size() > 1) {
                startOptions = new MarkerOptions().position(pointList.get(0)).
                        icon(startBitmap).zIndex(9).draggable(true);
            }

            // 路线
            if (pointList.size() >= 2) {

                polyLine = new PolylineOptions().width(10).color(Color.YELLOW).points(pointList);
            }

        }

        addMarker();
    }

    /**
     * 添加地图覆盖物
     */
    private void addMarker() {

        Log.i("TAG", "添加覆盖物");
        if (null != update) {
            baiduMap.setMapStatus(update);
        }
        //开始点覆盖物
        if (null != startOptions) {
            baiduMap.addOverlay(startOptions);
        }
        // 路线覆盖物
        if (null != polyLine) {
            baiduMap.addOverlay(polyLine);
        }
        // 实时点覆盖物
        if (null != realtimeOptions) {
            baiduMap.addOverlay(realtimeOptions);
        }
        //结束点覆盖物
        if (null != endOptions) {
            baiduMap.addOverlay(endOptions);
        }

    }


    /**
     * 初始化定位，设置定位参数
     */
    private void initLocation() {
        //用来设置定位sdk的定位方式
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        mLocationClient.setLocOption(option);

    }


    /**
     * 初始化监听器
     **/

    private void initListener() {
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (itemData.getVisibility() == View.GONE) {
                    itemData.setVisibility(View.VISIBLE);
                } else {
                    itemData.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 定位监听器
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                    bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) { //gps,网络定位成功定位

                double latitude = bdLocation.getLatitude(); //纬度
                double longitude = bdLocation.getLongitude(); // 经度
                int gpsAccuracyStatus = bdLocation.getGpsAccuracyStatus();//GPS信号强度


                //                double radius = bdLocation.getRadius(); //精度
                //                float speed = 0f;
                //                if(bdLocation.hasSpeed()){
                //                    speed = bdLocation.getSpeed();
                //                    speedList.add(speed);
                //
                //                    Log.i("TAG","速度"+speed);
                //                }
                latLng = new LatLng(latitude, longitude); //坐标点

                if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {

                } else {
                    if (pointList.size() < 1) { //初次定位

                        pointList.add(latLng);

                    } else {
                        LatLng lastPoint = pointList.get(pointList.size() - 1);//上一次定位坐标点
                        double rang = DistanceUtil.getDistance(lastPoint, latLng); // 两次定位的距离
                        if (rang > 10) {
                            distance = distance + rang;
                            pointList.add(latLng);
                        }
                    }
                    tvDistance.setText(GeneralUtil.doubleToString(distance) + "KM");
                    tvPeisu.setText(calPeisu(distance, time));//显示配速
                    tvSpeed.setText(String.valueOf(calAvgSpeed(distance, time)));//显示平均速度
                    tvKcal.setText(String.valueOf(calKcal(distance,userWeight)));//显示卡路里
                    showGpsState(gpsAccuracyStatus);

                }
                drawTrace(latLng);

            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) { //服务器错误
                Toast.makeText(RunActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(RunActivity.this, "网络错误，请连接网络", Toast.LENGTH_SHORT).show();
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(RunActivity.this, "定位错误，请设置手机模式", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 绘制最终完成地图
     */
    private void drawFinishMap() {
        if (pointList.size() == 0) {
            return;
        }//但没有轨迹点时，不绘制最终地图


        baiduMap.clear();

        LatLng startLatLng = pointList.get(0);
        LatLng endLatLng = pointList.get(pointList.size() - 1);

        //地理范围
        LatLngBounds bounds = new LatLngBounds.Builder().include(startLatLng).include(endLatLng).build();

        update = MapStatusUpdateFactory.newLatLngBounds(bounds);

        if (pointList.size() >= 2) {

            // 开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start_detail);
            startOptions = new MarkerOptions().position(startLatLng).
                    icon(startBitmap).zIndex(9).draggable(true);

            // 终点
            BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_end_detail);
            endOptions = new MarkerOptions().position(endLatLng)
                    .icon(endBitmap).zIndex(9).draggable(true);

            polyLine = new PolylineOptions().width(10).color(Color.RED).points(pointList);

        } else {
            //实时点
            realtimeBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            realtimeOptions = new MarkerOptions().position(startLatLng).icon(realtimeBitmap)
                    .zIndex(9).draggable(true);

        }


        addMarker();
    }

    /***
     * 计算配速
     * @param distance 距离 单位：公里(km)
     * @param time 体重 单位：公斤(kg)
     *
     **/
    private String calPeisu(double distance, int time) {
        //转化为千米（公里）
        double km = distance/1000;

        // 四舍五入，保留2为小数
        double d= Math.round(km*100)/100.0;
        if(d > 0){
            int a = (int) ((time) / d);//int类型配速
            peiSu = ChronometerUtils.formatPeisu(a);
            return peiSu;
        }else{
            return "0\'0\"";
        }


    }

    /***
     * 计算速度
     * @param distance 距离 单位：公里(km)
     * @param time 体重 单位：公斤(kg)
     *
     **/

    private double calAvgSpeed(double distance, int time) {
        //转化为千米（公里）
        double km = distance/1000;

        // 四舍五入，保留2为小数
        double d= Math.round(km*100)/100.0;

        if(d >0){
            double a = d / (time / 3600);
            // 四舍五入，保留2位小数
            double avgSpeed = Math.round(a * 100) / 100.0;
            return avgSpeed;
        }else{
            return 0.0;
        }

    }


    /**
     * 计算卡路里
     * @param distance 距离 单位:公里（km）
     * @param weight 体重 单位:公斤(kg)
     * **/

    private int calKcal(double distance,double weight){
        //转化为千米（公里）
        double km = distance/1000;

        // 四舍五入，保留2为小数
        double d= Math.round(km*100)/100.0;
        if(d > 0){
            double k = 1.036;//跑步k值
            int kcal =(int)(k * d * weight);
            return kcal;
        }else{
            return 0;
        }

    }


    /**
     * 显示dialog
     **/
    private void showDialog() {

        new com.rentalphang.runj.ui.AlertDialog(this, "结束跑步", "确定将结束跑步？", true, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive) {
                    drawFinishMap();
                    saveRunRecord();
                    mManager.stopUploadAuto();
                    btnTracePause.setVisibility(View.INVISIBLE);
                    btnTraceContinue.setVisibility(View.INVISIBLE);
                    btnTraceStop.setVisibility(View.INVISIBLE);
                    mLocationClient.stop();
                    btnTraceShare.setVisibility(View.VISIBLE);
                    clearAll();
                }

            }
        }).show();

    }

    /**
     * 判断GPS信号质量
     ***/
    private void showGpsState(int stateId) {
        if(GeneralUtil.isOpenGPS(context)){
            switch (stateId) {
                case GPS_ACCURACY_UNKNOWN:
                    ivGpsState.setImageResource(R.mipmap.ic_gps_unknow);
                    tvGpsTips.setText("GPS信号未知，正在搜寻···");
                    break;

                case GPS_ACCURACY_BAD:
                    ivGpsState.setImageResource(R.mipmap.ic_gps_bad);
                    tvGpsTips.setText("GPS清号弱，请在空旷的地方跑步");
                    break;

                case GPS_ACCURACY_MID:
                    ivGpsState.setImageResource(R.mipmap.ic_gps_normal);
                    tvGpsTips.setText("GPS信号良好");
                    break;

                case GPS_ACCURACY_GOOD:
                    ivGpsState.setImageResource(R.mipmap.ic_gps_full);
                    tvGpsTips.setText("GPS信号非常好，请尽情跑步");
                    break;

            }

        }else{
            ivGpsState.setImageResource(R.mipmap.ic_gps_off);
            tvGpsTips.setText("GPS未开启，正使用基站定位");
        }

    }

    /***
     * 保存跑步记录
     **/

    private void saveRunRecord() {
        runRecord = new RunRecord();
        String id = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        runRecord.setRecordId(id);
        runRecord.setPoints(pointList);
        runRecord.setDistance(distance);
        runRecord.setTime(time);
        runRecord.setAvgSpeed(avgSpeed);
        runRecord.setPeiSu(peiSu);
        runRecord.setUserId((String) BmobUser.getObjectByKey(context,"objectId"));
        runRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (GeneralUtil.isNetworkAvailable(context)) {
            runRecord.setSync(true);
            runRecord.save(context,new SaveListener() {
                @Override
                public void onSuccess() {
                    System.out.println("上传到云端");
                    Message msg = new Message();
                    msg.what = IdentiferUtil.SAVE_DATA_TO_BMOB_SUCCESS;
                    handler.sendMessage(msg);
                }

                @Override
                public void onFailure(int i, String s) {
                    System.out.println("没有上传到云端");
                    Log.i("TAG", "没有上传到云端");
                    Message msg = new Message();
                    msg.what = IdentiferUtil.SAVE_DATA_TO_BMOB_FAILURE;
                    handler.sendMessage(msg);
                }
            });
        } else {
            System.out.println("本地备份");
            runRecord.setSync(false);
            DBManager.getInstance(context).insertRunRecord(runRecord);


        }


    }

    /***
     * 初始化当前用户信息
     * */
    private void initCurrentUserInfo(){
        userNickName = (String) BmobUser.getObjectByKey(context,"nickName");
        userWeight = (int) user.getWeight();
    }

    @Override
    public RadarUploadInfo onUploadInfoCallback() {
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = distance + "KM";
        info.pt = latLng;//坐标点
        return info;
    }

    /***
     * 初始化雷达
     */
    private void initRadar() {

        mManager = RadarSearchManager.getInstance();
        mManager.setUserID(userNickName);
        //设置自动上传的callback和时间间隔
        mManager.startUploadAuto(this, 5000);
    }

    /**
     * 清除所有雷达信息
     * **/
    private void clearAll() {
        //清除用户信息
        mManager.clearUserInfo();
        //释放资源
        mManager.destroy();
        mManager = null;
    }

    /**
     * 地图截屏
     */

    private void mapScreenShot(){

        baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                //将bitmap存储到文件中
                Log.i("TAG", "截图成功");
                snapShotPath = FileUtil.saveBitmapToFile(bitmap, "mapshot");

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(btnTraceShare.getVisibility() != View.VISIBLE){
                ToastUtil.setShortToast(context,"请先结束运动");
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
