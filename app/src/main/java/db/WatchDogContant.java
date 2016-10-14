package db;

/**
 * Created by Sin on 2016/9/19.
 */
public interface WatchDogContant {
    // 数据库名称
    public static String WATCHDOG_DBNAME = "watchdog.db";
    // 版本号
    public static int WATCHDOG_DBVERSION = 1;
    //表名
    public static String WATCHDOG_TABLENAME = "info";
    //id
    public static String WATCHDOG_ID = "_id";
    //软件的包名
    public static String WATCHDOG_PACKAGENAME = "packageName";
    // 创建表的sql语句
    public static String WATCHDOG_CREATETABLESQL = "create table "
            +WATCHDOG_TABLENAME + "(" + WATCHDOG_ID
            + " integer primary key autoincrement," + WATCHDOG_PACKAGENAME
            + " varchar(50))";
}
