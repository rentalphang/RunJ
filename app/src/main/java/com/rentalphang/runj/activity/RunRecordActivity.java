package com.rentalphang.runj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.RunRecordListAdapter;
import com.rentalphang.runj.db.DBManager;
import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.model.biz.ActivityManager;
import com.rentalphang.runj.utils.GeneralUtil;
import com.rentalphang.runj.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;



/**
 * Created by rentalphang on 2016/12/6.
 */

public class RunRecordActivity extends BaseActivity {

    private RunRecordListAdapter mAdapter;
    private List<RunRecord> data = new ArrayList<>(); //本地数据
    private List<RunRecord> serverData = new ArrayList<>(); //服务器数据

    private static final int Request_Code = 0x11;


    private SwipeRefreshLayout swipeRefreshLayout; //下拉刷新组件

    private ListView mListView;

    private LinearLayout syncLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_record);
        ActivityManager.getInstance().pushOneActivity(this);

        initComponent();
        getdata();
        setAdapter();
        setListener();
        showSyncLayout();
    }



    /**
     * 初始化组件
     */
    private void initComponent() {

        syncLayout = (LinearLayout) findViewById(R.id.run_record_sync_identifer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.run_record_swiperefreshlayout);
        mListView = (ListView) findViewById(R.id.run_record_listview);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

    }



    /**
     * 获取数据
     */
    private void getdata(){

        //获取本地数据
        data = DBManager.getInstance(context).getRunRecords();
        //获取服务器数据
        getDataFromServer();


    }

    private boolean hasSync(){
        if(data!= null) {
            for (RunRecord runRecord:data){
                if(!runRecord.isSync()) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (data != null) {
            mAdapter = new RunRecordListAdapter(context, data);
            mListView.setAdapter(mAdapter);
        }
    }

    /**
     * 设置监听
     */
    private void setListener(){

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(RunRecordActivity.this,RunRecordDetailActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent, Request_Code);
            }
        });

        //刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (GeneralUtil.isNetworkAvailable(context)) {
                    //同步数据
                    syncData();
                    showSyncLayout();
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context,"网络状态异常，请稍后重试",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void showSyncLayout(){
        if(hasSync()) {
            syncLayout.setVisibility(View.VISIBLE);
        } else {
            syncLayout.setVisibility(View.GONE);
        }
    }



    /**
     * 同步数据
     */
    private void syncData(){

        //同步网络数据到本地

        if(data.size()<=0&&serverData.size()>0) {//本地数据为空

            data = serverData;
            //保存到数据库中
            for (RunRecord runRecord :serverData) {
                DBManager.getInstance(context).insertRunRecord(runRecord);
            }

        } else if(data.size()>0&&serverData.size()<=0) {//网络无数据

            for(final RunRecord record:data){


                record.save(context,new SaveListener() {
                    @Override
                    public void onSuccess() {
                        //修改数据库数据信息
                        record.setSync(true);
                        DBManager.getInstance(context).updateOneRunRecord(record.getRecordId()
                                ,record.getObjectId(),true);
                        ToastUtil.setShortToast(context,"同步数据成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            serverData = data;

        } else if (data.size()>0&&serverData.size()>0) {//都有数据

            for(final RunRecord record:data){

                if(!serverData.contains(record)) { //网络数据中不包含这条记录

                    record.save(context,new SaveListener() {
                        @Override
                        public void onSuccess() {
                            record.setSync(true);
                            DBManager.getInstance(context).updateOneRunRecord(record.getRecordId(),record.getObjectId(),true);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }

                    });
                }
            }
            for(RunRecord runRecord:serverData) {

                if (!data.contains(runRecord)) {//本地数据没有
                    data.add(runRecord);
                    DBManager.getInstance(context).insertRunRecord(runRecord);
                }
            }
        } else {

        }
        swipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();

    }
    /**
     * 从服务器端获取数据
     */
    private void getDataFromServer(){
        BmobQuery<RunRecord> query = new BmobQuery<>();
        String objectId = (String) BmobUser.getObjectByKey(context,"objectId");
        query.addWhereEqualTo("userId",objectId);
        query.findObjects(context,new FindListener<RunRecord>() {
            @Override
            public void onSuccess(List<RunRecord> list) {
                serverData = list;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch ( requestCode) {
            case Request_Code:
                getdata();
                mAdapter.notifyDataSetChanged();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



}
