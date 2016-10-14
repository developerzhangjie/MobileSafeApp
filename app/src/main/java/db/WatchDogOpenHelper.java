package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sin on 2016/9/19.
 */
public class WatchDogOpenHelper extends SQLiteOpenHelper {

    // 一般在构造函数中设置数据库的名称和版本号
    public WatchDogOpenHelper(Context context) {
        super(context, WatchDogContant.WATCHDOG_DBNAME, null, WatchDogContant.WATCHDOG_DBVERSION);
    }

    // 创建表结构
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WatchDogContant.WATCHDOG_CREATETABLESQL);
    }

    // 更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
