package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import db.WatchDogContant;
import db.WatchDogOpenHelper;

/**
 * Created by Sin on 2016/10/10.
 * Description:
 */

public class WatchDogDao {
    private WatchDogOpenHelper watchDogOpenHelper;

    public WatchDogDao(Context context) {
        watchDogOpenHelper = new WatchDogOpenHelper(context);
    }

    /**
     * Description:添加程序
     *
     * @return true:表示添加成功,false:表示添加失败
     * packageName : 表示添加的应用程序的包名
     */
    public boolean addLockAPP(String packageName) {
        // 1.获取数据库
        SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
        // 2.添加操作
        ContentValues contentValues = new ContentValues();
        contentValues.put(WatchDogContant.WATCHDOG_PACKAGENAME, packageName);
        long insert = database.insert(WatchDogContant.WATCHDOG_TABLENAME, null, contentValues);
        database.close();
        return insert != -1;
    }

    /**
     * Description:删除包名
     *
     * @return : true:删除成功 false:表示删除失败
     */
    public boolean deleteLockApp(String packageName) {
        // 获取数据库
        SQLiteDatabase database = watchDogOpenHelper.getWritableDatabase();
        // table : 表名
        // whereClause : 查询条件 "blacknumber=?"
        // whereArgs:查询条件的参数
        int delete = database.delete(WatchDogContant.WATCHDOG_TABLENAME, WatchDogContant.WATCHDOG_PACKAGENAME + "=?", new String[]{packageName});
        // 关闭数据库
        database.close();
        return delete != 0;
    }

    /**
     * Description:查询数据库中是否有应用程序包名，加锁返回true，没加锁返回false
     */
    public boolean queryLockAPP(String packageName) {
        boolean islock = false;
        // 获取数据库
        SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
        // 查询数据库
        Cursor cursor = database.query(WatchDogContant.WATCHDOG_TABLENAME, null, WatchDogContant.WATCHDOG_PACKAGENAME + "=?", new String[]{packageName}, null, null, null);
        // 解析cursor,如果cursor中就只有一条数据,不用while
        if (cursor != null) {
            if (cursor.moveToNext()) {
                islock = true;
            }
            cursor.close();
        }
        database.close();
        return islock;
    }

    /**
     * Description:查询所有应用程序的包名
     */
  /*  public List<String> queryAllLockApp() {
        SystemClock.sleep(2000);
        List<String> list = new ArrayList<>();
        // 获取数据
        SQLiteDatabase database = watchDogOpenHelper.getReadableDatabase();
        //查询全部数据
        Cursor cursor = database.query(WatchDogContant.WATCHDOG_TABLENAME, new String[]{WatchDogContant.WATCHDOG_PACKAGENAME}, null, null, null, null, null);
        //遍历cursor
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //获取数据
                String packageName = cursor.getString(0);
                list.add(packageName);
            }
            cursor.close();
        }
        database.close();
        return list;
    }*/
}
