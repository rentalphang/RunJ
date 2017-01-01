package com.rentalphang.runj.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.RadarRecycleAdapter;
import com.rentalphang.runj.adapter.SearchUserRecyclerAdapter;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.ToastUtil;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import static android.R.attr.data;
import static com.rentalphang.runj.R.id.sw_refresh;


/**
 * Created by rentalphang on 2016/7/29.
 */
public class DiscoverFragment extends Fragment implements RadarSearchListener, BDLocationListener {

    private View mRootView;
    // 定位相关
    LocationClient mLocClient;
    private int pageIndex = 0;
    private LatLng pt = null;
    public User user;

    // 周边雷达相关
    RadarNearbyResult listResult = null;
    RadarRecycleAdapter mRadarAdapter = null;
    private String userID = "";
    public List<RadarNearbyInfo> list;
    private List<String> userAvaterList;
    private SwipeRefreshLayout dis_refresh;
    private RecyclerView rc_view;
    private TextView tvNull;

    /**
     * 查询用户成功
     */
    private static final int Query_Radar_Success = 0x11;
    private static final int Query_Radar_Failure = 0x12;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_Radar_Success:
                    dis_refresh.setRefreshing(false);
                    ToastUtil.setShortToast(getActivity(), "查询周边成功");
                    parseResultToList(listResult);
                    break;

                case Query_Radar_Failure:
                    dis_refresh.setRefreshing(false);
                    ToastUtil.setShortToast(getActivity(), "查询周边失败");
                    break;

            }
            super.handleMessage(msg);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 周边雷达设置监听
        RadarSearchManager.getInstance().addNearbyInfoListener(this);
//        // 周边雷达设置用户，id为空默认是设备标识
//        RadarSearchManager.getInstance().setUserID(userID);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        if (mRootView == null) {//避免重复加载
            mRootView = inflater.inflate(R.layout.fragment_discover, null);
            user = BmobUser.getCurrentUser(getActivity(), User.class);
            initComponent();
        }

        ViewGroup parents = (ViewGroup) mRootView.getParent();
        if (parents != null) {
            parents.removeView(mRootView);
        }

        dis_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchNearby();
//                query(list);
            }
        });

        return mRootView;
    }


    /**
     * 初始化组件
     **/
    private void initComponent() {
//        tvNull = (TextView) mRootView.findViewById(R.id.tv_discover_null);
        dis_refresh = (SwipeRefreshLayout) mRootView.findViewById(R.id.discover_refresh);
        rc_view = (RecyclerView) mRootView.findViewById(R.id.rv_radaruser);
        mRadarAdapter = new RadarRecycleAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(linearLayoutManager);
        rc_view.setAdapter(mRadarAdapter);
        dis_refresh.setEnabled(true);
        dis_refresh.setColorSchemeResources(R.color.colorPrimaryDark);

    }

    /**
     * 更新结果列表
     *
     * @param res
     */
    public void parseResultToList(RadarNearbyResult res) {
        if (res == null) {
            if (mRadarAdapter.list != null) {
                mRadarAdapter.list.clear();
                mRadarAdapter.notifyDataSetChanged();
            }


        } else {
            list = res.infoList;
            mRadarAdapter.list = res.infoList;
//
//            if (res.infoList.size() >= 1) {
//                rc_view.setVisibility(View.VISIBLE);
//                tvNull.setVisibility(View.GONE);
//            }
            mRadarAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 查找周边的人
     */
    public void searchNearby() {
        if (pt == null) {
            Toast.makeText(getActivity(), "未获取到位置", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        pageIndex = 0;
        searchRequest();
    }

    private void searchRequest() {
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(pt).pageNum(pageIndex).radius(2000).pageCapacity(11);
        RadarSearchManager.getInstance().nearbyInfoRequest(option);

    }

    private void query(List<RadarNearbyInfo> list) {
        if (list != null && list.size() > 0) {
            userAvaterList = new ArrayList<>();
            for (final int[] i = {0}; i[0] < list.size(); i[0]++) {//将百度地图雷达userID(nickName)装入数组
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("nickName", list.get(i[0]).userID);
                query.findObjects(getActivity(), new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        userAvaterList.add(list.get(i[0]).getHeadImgUrl());
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        } else {
            return;
        }


    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            return;
        }
        pt = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        // TODO Auto-generated method stub
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            // 获取成功
            listResult = result;
            handler.sendEmptyMessage(Query_Radar_Success);

        } else {
            handler.sendEmptyMessage(Query_Radar_Failure);
        }


    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }
}
