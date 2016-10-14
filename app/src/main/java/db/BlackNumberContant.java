package db;

/**
 * Created by Sin on 2016/9/19.
 */
public interface BlackNumberContant {
    //创建的数据库的名称
    public static String BLACKNUMBER_DBNAME = "blackNumber.db";
    //创建数据库的版本
    public static int BLACKNUMBER_DBVERSION = 1;
    //表名
    public static String BLACKNUMBER_TABLENAME = "blackNumberData";
    //id
    public static String BLACKNUMBER_ID = "_id";
    //黑名单号码字段
    public static String BLACKNUMBER_NUMBER = "blacknumber";
    //拦截模式
    public static String BLACKNUMBER_MODE = "mode";
    //创建表sql语句
    public static String BLACKNUMBER_CREATETABLESQL = "create table "
            + BLACKNUMBER_TABLENAME + "(" + BLACKNUMBER_ID
            + " integer primary key autoincrement," + BLACKNUMBER_NUMBER
            + " varchar(20)," + BLACKNUMBER_MODE + " varchar(2))";
    //拦截电话号码
    public static int BLACKNUMBER_CALL = 0;
    //拦截短信
    public static int BACKNUMBER_SMS = 1;
    //拦截全部
    public static int BALCKNUMBER_ALL = 2;

}
