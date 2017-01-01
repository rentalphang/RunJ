package com.rentalphang.runj.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rentalphang.runj.model.bean.RunRecord;
import com.rentalphang.runj.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库管理类，单例类
 *
 * Created by rentalphang on 2016/11/14.
 */

public class DBManager {
    public Context context;

    private static DBManager dbManager;

    private static DBOpenHelper dbOpenHelper;

    private SQLiteDatabase db;

    private List<RunRecord> runRecords = null;//runrecord表集合

    private DBManager(Context context) {
        this.context = context;
    }

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
            dbOpenHelper = new DBOpenHelper(context, "RunJ.db", null, 1);
        }
        return dbManager;
    }

    public List<RunRecord> getRunRecords() {
        if (runRecords == null) {
            runRecords = new ArrayList<>();
            //从数据库获取
            runRecords = getAllRunRecord();
        }
        return runRecords;
    }

    /**
     * 增加跑步记录
     * **/

    public void insertRunRecord(RunRecord runRecord){
        if(runRecord == null){
            return;
        }
        try{
            db = dbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("objectid",runRecord.getObjectId());
            values.put("recordid",runRecord.getRecordId());
            values.put("userid",runRecord.getUserId());
            values.put("time",runRecord.getTime());
            values.put("distance",runRecord.getDistance());
            values.put("peisu",runRecord.getPeiSu());
            values.put("avgspeed",runRecord.getAvgSpeed());
            values.put("kcal",runRecord.getKcal());
            values.put("points", JsonUtil.listTojson(runRecord.getPoints()));
            values.put("issync", runRecord.isSync());
            values.put("createtime",runRecord.getCreateTime());
            db.insert("runrecord",null,values);
            values.clear();
            runRecords.add(runRecord);
            db.setTransactionSuccessful();
            Log.i("TAG","成功插入数据库");

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }

    }

    /***
     * 从数据库获取所有的跑步记录
     * **/
    private List<RunRecord> getAllRunRecord(){
        List<RunRecord> records = new ArrayList<>();
        String sql = "select * from runrecord";
        db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();


        try{
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                do{
                    RunRecord runRecord = new RunRecord();
                    runRecord.setObjectId(cursor.getString(cursor.getColumnIndex("objectid")));
                    runRecord.setUserId(cursor.getString(cursor.getColumnIndex("userid")));
                    runRecord.setRecordId(cursor.getString(cursor.getColumnIndex("recordid")));
                    runRecord.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                    runRecord.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
                    runRecord.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex("avgspeed")));
                    runRecord.setKcal(cursor.getInt(cursor.getColumnIndex("kcal")));
                    runRecord.setPeiSu(cursor.getString(cursor.getColumnIndex("peisu")));
                    runRecord.setPoints(JsonUtil.jsonToListPoint(cursor.getString(cursor.getColumnIndex("points"))));
                    runRecord.setCreateTime(cursor.getString(cursor.getColumnIndex("createtime")));
                    int isSync = cursor.getInt(cursor.getColumnIndex("issync"));
                    if(isSync == 1){
                        runRecord.setSync(true);
                    }else{
                        runRecord.setSync(false);
                    }
                    records.add(runRecord);
                }while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }

        return records;
    }


    /**
     * 同步数据时，更新数据库信息
     *
     * **/
    public void updateOneRunRecord(String recordId,String objectId,boolean isSync){
        db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            values.put("objectid",objectId);
            values.put("recordId",recordId);
            db.update("runrecord",values,"recordid=?",new String[]{recordId});
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    /**
     * 删除一条跑步记录
     *
     * ***/
    public void deleteOneRunRecord(int position){
        String recordId = runRecords.get(position).getRecordId();
        db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            String sql = "delete from runrecord where recordid = "+recordId;
            db.execSQL(sql);
            runRecords.remove(position);
            Log.i("TAG","删除成功");
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }





}
