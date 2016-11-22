package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bean.BlackNumberInfo;
import db.BlackNumberConstant;
import db.BlackNumberOpenHelper;

/**
 * Description:数据库的具体操作
 * Created by Sin on 2016/9/19.
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper blackNumberOpenHelper;

    //增删改查
    //1.获取数据库BlackNumberOpenHelper
    public BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //添加黑名单
    //返回true，就是添加成功，返回false就是添加失败
    public boolean addBlackNumber(String blackNumber, int mode) {
        //1.获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
        //2.添加操作
        ContentValues contentValues = new ContentValues();
        contentValues.put(BlackNumberConstant.BLACKNUMBER_NUMBER, blackNumber);
        contentValues.put(BlackNumberConstant.BLACKNUMBER_MODE, mode);
        long insert = database.insert(BlackNumberConstant.BLACKNUMBER_TABLENAME, null, contentValues);
        //contentValues.clear();
        database.close();
        return insert != -1;
    }

    //删除黑名单
    //返回true，就是删除成功，返回false就是删除失败
    public boolean deleteBlackNumber(String blackNumber) {
        //1.获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
        //2.添加操作
        int delete = database.delete(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                BlackNumberConstant.BLACKNUMBER_NUMBER + "=?",
                new String[]{blackNumber});
        /*database.delete(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                BlackNumberConstant.BLACKNUMBER_NUMBER + "=?",
                new String[]{blackNumber});*/
        database.close();
        return delete != 0;
    }

    //更新黑名单
    //返回true，就是更新成功，返回false就是更新失败
    public boolean updateBlackNumber(String blackNumber, int mode) {
        //1.获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
        //2.添加操作
        ContentValues values = new ContentValues();
        values.put(BlackNumberConstant.BLACKNUMBER_MODE, mode);
        int update = database.update(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                values,
                BlackNumberConstant.BLACKNUMBER_NUMBER + "=?",
                new String[]{blackNumber});
        /*database.update(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                values,
                BlackNumberConstant.BLACKNUMBER_NUMBER + "=?",
                new String[]{blackNumber});*/
        database.close();
        return update != 0;
    }

    //查询指定黑名单
    public int queryBlackNumber(String blackNumber) {
        int mode = -1;
        //1.获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
        //2.添加操作
        Cursor cursor = database.query(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                new String[]{BlackNumberConstant.BLACKNUMBER_MODE},
                BlackNumberConstant.BLACKNUMBER_NUMBER + "=?",
                new String[]{blackNumber},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                mode = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        return mode;
    }

    //查询所有黑名单
    public List<BlackNumberInfo> queryAllBlackNumber() {
        List<BlackNumberInfo> list = new ArrayList<>();
        //获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
        //添加操作
        Cursor cursor = database.query(BlackNumberConstant.BLACKNUMBER_TABLENAME,
                new String[]{BlackNumberConstant.BLACKNUMBER_NUMBER,
                        BlackNumberConstant.BLACKNUMBER_MODE},
                null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                int mode = cursor.getInt(1);
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.number = number;
                blackNumberInfo.mode = mode;
                list.add(blackNumberInfo);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    /**
     * description:查询部分数据
     * maxNumber:查询数量
     * index：查询起始位置
     * Created by Sin on 2016/9/19.
     */
    public List<BlackNumberInfo> queryPartBlackNumber(int maxNumber, int index) {
        List<BlackNumberInfo> list = new ArrayList<>();
        //获取数据库
        SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select blacknumber,mode from blackNumberData limit ? offset ?",
                new String[]{maxNumber + "", index + ""});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                int mode = cursor.getInt(1);
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.number = number;
                blackNumberInfo.mode = mode;
                list.add(blackNumberInfo);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

}
