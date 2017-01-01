package com.rentalphang.runj.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rentalphang on 2016/8/22.
 */
public class DBOpenHelper extends SQLiteOpenHelper{

    private Context context;

    /**
     * 创建表runrecord
     */
    public static final String CREATE_TABLE_RUNRECORD ="create table runrecord ("+
            "recordid text,"+
            "objectid text,"+
            "userid text,"+
            "time real,"+
            "distance real,"+
            "peisu real,"+
            "kcal real,"+
            "avgspeed real,"+
            "points text,"+
            "issync numeric,"+
            "createtime text)";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建记录表
        db.execSQL(CREATE_TABLE_RUNRECORD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists runrecord");
        onCreate(db);

    }
}
