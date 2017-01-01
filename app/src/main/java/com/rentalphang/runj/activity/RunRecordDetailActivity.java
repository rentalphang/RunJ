package com.rentalphang.runj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.rentalphang.runj.R;
import com.rentalphang.runj.db.DBManager;
import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.model.biz.ActivityManager;
import com.rentalphang.runj.utils.FileUtil;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.IdentiferUtil;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * Created by rentalphang on 2016/12/6.
 */

public class RunRecordDetailActivity extends BaseActivity implements View.OnClickListener{

    private MapView mapView;

    private TextView tvCreateTime;
    private TextView tvDistance;
    private TextView tvTime;
    private TextView tvPeisu;
    private TextView tvKcal;

    private ImageView ivBack;
    private ImageView ivDelete;
    private ImageView ivShare;



    private BaiduMap baiduMap;

    private RunRecord runRecord = null;

    private List<LatLng> points = null; //定位点集合

    private int position; //位置

    private String snapShotPath = null;


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
            switch (msg.what) {
                case IdentiferUtil.DELETE_RUN_RECORD_SUCCESS : //删除成功

                    Toast.makeText(context,"删除成功！",Toast.LENGTH_SHORT).show();
                    break;
                case IdentiferUtil.DELETE_RUN_RECORD_FAILURE: //删除失败

                    break;
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_reord_detail);
        SDKInitializer.initialize(context);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();
        baiduMap = mapView.getMap();
        position = getIntent().getIntExtra("position", 0);
        //获取到目标记录
        runRecord = DBManager.getInstance(context).getRunRecords().get(position);
        //获取到记录坐标集合
        points = runRecord.getPoints();
        setData();
        if(points!=null && points.size()>0) {
            //绘制轨迹
            drawTrack();


        }

    }


    /**
     * 初始化
     */
    private void initComponent() {

        mapView = (MapView) findViewById(R.id.record_details_mapview);
        mapView.showZoomControls(false);

        tvCreateTime = (TextView) findViewById(R.id.tv_record_details_create_time);
        tvDistance = (TextView) findViewById(R.id.tv_record_details_distance);
        tvTime = (TextView) findViewById(R.id.tv_record_details_time);
        tvPeisu = (TextView) findViewById(R.id.tv_record_details_peisu);
        tvKcal = (TextView) findViewById(R.id.tv_record_details_kcal);
        ivShare = (ImageView) findViewById(R.id.iv_run_detail_share);
        ivDelete = (ImageView) findViewById(R.id.iv_run_detail_delete);
        ivBack = (ImageView) findViewById(R.id.iv_run_detail_back);

        ivBack.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivShare.setOnClickListener(this);

    }

    /**
     * 绘制轨迹
     */
    private void drawTrack(){
        baiduMap.clear();

        LatLng  startLatLng = points.get(0);
        LatLng  endLatLng = points.get(points.size() - 1);

        //地理范围
        //        LatLngBounds bounds = new LatLngBounds.Builder().include(startLatLng).include(endLatLng).build();

        MapStatus mapStatus = new MapStatus.Builder().target(startLatLng).zoom(17).build();

        update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        if (points.size()>=2) {

            // 开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start_detail);
            startOptions = new MarkerOptions().position(startLatLng).
                    icon(startBitmap).zIndex(9).draggable(true);

            // 终点
            BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_end_detail);
            endOptions = new MarkerOptions().position(endLatLng)
                    .icon(endBitmap).zIndex(9).draggable(true);

            polyLine = new PolylineOptions().width(10).color(Color.GREEN).points(points);
        }else {
            //实时点
            realtimeBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point);
            realtimeOptions = new MarkerOptions().position(startLatLng).icon(realtimeBitmap)
                    .zIndex(9).draggable(true);

        }

        addMark();

    }

    /**
     * 添加覆盖物
     */
    private void addMark(){

        if (null != update) {
            baiduMap.setMapStatus(update);
        }
        //开始点覆盖物
        if (null != startOptions ) {
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
        if (null != endOptions ) {
            baiduMap.addOverlay(endOptions);
        }
    }

    private void setData(){
        tvCreateTime.setText(runRecord.getCreateTime());
        tvDistance.setText(GeneralUtil.doubleToString(runRecord.getDistance()));
        tvTime.setText(GeneralUtil.secondsToString(runRecord.getTime()));
        tvPeisu.setText(runRecord.getPeiSu());
        tvKcal.setText(runRecord.getKcal() +"");
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_run_detail_delete://删除记录
                new com.rentalphang.runj.ui.AlertDialog(this, "删除记录", "确定永久删除该条跑步数据(不可恢复)？", true, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogButtonClick(int requestCode, boolean isPositive) {
                        if (isPositive) {
                            deleteRecord();
                        }

                    }
                }).show();

                break;


            case R.id.iv_run_detail_share://分享记录
                mapScreenShot();//截图

                ShareSDK.initSDK(context);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // title标题：微信、QQ（新浪微博不需要标题）
                oks.setTitle("今天，你跑了吗？");  //最多30个字符
                // text是分享文本：所有平台都需要这个字段
                oks.setText(runRecord.getCreateTime() + "我跑了" + GeneralUtil.distanceToKm(runRecord.getDistance()) + "千米"
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
     * 删除记录
     */
    private void deleteRecord() {


        boolean isSync = false;

        isSync = runRecord.isSync();

        if(GeneralUtil.isNetworkAvailable(context)) { //有网络
            if (isSync) { //已同步

                //从服务器端删除

                RunRecord record = new RunRecord();
                record.setObjectId(runRecord.getObjectId());
                record.delete(context,new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        Message msg = new Message();
                        msg.what = IdentiferUtil.DELETE_RUN_RECORD_SUCCESS;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });


            }
            DBManager.getInstance(context).deleteOneRunRecord(position);

        } else { //无网络
            if(isSync) {
                Toast.makeText(context,"数据已同步，请联网后删除",Toast.LENGTH_SHORT).show();
            } else {
                DBManager.getInstance(context).deleteOneRunRecord(position);
            }
        }

        RunRecordDetailActivity.this.finish();

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


}
