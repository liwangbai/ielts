package com.iyuba.ieltslistening.dao.dbopenhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBOpenHelper";
    private final String dbName;

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if ("paper_list".equals(dbName)) {
            db.execSQL("create table paper_list(_id integer primary key," +
                    "download_state integer(4)," +
                    "is_download integer(4)," +
                    "is_free integer(4)," +
                    "is_vip integer(4)," +
                    "name varchar(200)," +
                    "product_id integer(4)," +
                    "progress integer(4)," +
                    "version integer(4)," +
                    "test_time varchar(200))");
            db.execSQL("create table section_list(_id Long primary key," +
                    "paper_id integer(4)," +
                    "title_num1 varchar(100)," +
                    "part_type varchar(100)," +
                    "title_name varchar(100)," +
                    "sound varchar(100))");
            Log.d(TAG, "onCreate: create DB ---> paper_list");
        }else {
            Log.d(TAG, "onCreate: create other db");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
