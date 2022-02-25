package com.iyuba.ieltslistening.dao.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iyuba.ieltslistening.dao.TestPaperDao;
import com.iyuba.ieltslistening.dao.dbopenhelper.DBOpenHelper;
import com.iyuba.ieltslistening.pojo.Sections;
import com.iyuba.ieltslistening.pojo.TestPaper;

import java.util.ArrayList;
import java.util.List;

public class TestPaperDaoImpl implements TestPaperDao {

    private static final String TAG = "TestPaperDaoImpl";
    private final SQLiteDatabase database;

    public TestPaperDaoImpl(Context context, String databaseName) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, databaseName, null, 1);
        database = dbOpenHelper.getWritableDatabase();
    }

    @Override
    public Integer findExistByTitleID(int id) {
        Integer res = null;
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("select 1 from paper_list where _id = ? LIMIT 1", new String[]{String.valueOf(id)});

        while (cursor.moveToNext())
            res = cursor.getInt(0);
        return res;
    }

    @Override
    public void addTestPager(TestPaper testPaper) {
        int isDownload = 0;
        int isFree = 0;
        int isVip = 0;
        if (testPaper.isDownload())
            isDownload = 1;
        if (testPaper.isFree())
            isFree = 1;
        if (testPaper.isVip())
            isVip = 1;
        String sqlPre = "replace into paper_list (_id, download_state, is_download, is_free, is_vip, name, product_id, progress, version, test_time) values";
        String param = "(" + "'" + testPaper.getId() + "'" + ","
                + "'" + testPaper.getDownloadState() + "'" + ","
                + "'" + isDownload + "'" + ","
                + "'" + isFree + "'" + ","
                + "'" + isVip + "'" + ","
                + "'" + testPaper.getName() + "'" + ","
                + "'" + testPaper.getProductID() + "'" + ","
                + "'" + testPaper.getProgress() + "'" + ","
                + "'" + testPaper.getVersion() + "'" + ","
                + "'" + testPaper.getTestTime() + "'" + ")";
        String sql = sqlPre + param;
        Log.d(TAG, "add paper sql ===========>" + sql);
        if (findExistByTitleID(testPaper.getId()) == null)
            database.execSQL(sql);
    }

    @Override
    public List<TestPaper> findAllTestPaper() {
        List<TestPaper> dataList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("select * from paper_list", new String []{});
        while (cursor.moveToNext()) {
            boolean isDownload = false;
            boolean isFree = false;
            boolean isVip = false;
            if (cursor.getInt(2) == 1)
                isDownload = true;
            if (cursor.getInt(3) == 1)
                isFree = true;
            if (cursor.getInt(4) == 1)
                isVip = true;
            dataList.add(new TestPaper(cursor.getInt(1), cursor.getInt(0), isDownload, isFree, isVip, cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getString(9), cursor.getInt(8)));
        }
        return dataList;
    }

    @Override
    public Integer findSectionExistByPaperId(int id) {
        Integer res = null;
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("select 1 from section_list where paper_id = ? LIMIT 1", new String[]{String.valueOf(id)});

        while (cursor.moveToNext())
            res = cursor.getInt(0);
        return res;
    }

    @Override
    public void addSection(Sections sections, int paperId) {
        String sqlPre = "replace into section_list (_id, paper_id, title_num1, part_type, title_name, sound) values";
        String param = "(" + "'" + sections.getTitleNum() + "'" + ","
                + "'" + paperId + "'" + ","
                + "'" + sections.getTitleNum1() + "'" + ","
                + "'" + sections.getPartType() + "'" + ","
                + "'" + sections.getTitleName() + "'" + ","
                + "'" + sections.getSound() + "'" + ")";
        String sql = sqlPre + param;
        Log.d(TAG, "add sections sql ===========>" + sql);
        if (findExistByTitleID((int)sections.getTitleNum()) == null)
            database.execSQL(sql);
    }

    @Override
    public List<Sections> findSectionsByPaperId(int paperId) {
        List<Sections> dataList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("select * from section_list where paper_id = ?", new String []{String.valueOf(paperId)});
        while (cursor.moveToNext()) {
            dataList.add(new Sections(cursor.getLong(0), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        return dataList;
    }
}
