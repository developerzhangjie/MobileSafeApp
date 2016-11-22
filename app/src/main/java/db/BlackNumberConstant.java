package db;

/**Description:数据库常量
 * Created by Sin on 2016/9/19.
 */
public interface BlackNumberConstant {
    //创建的数据库的名称
    String BLACKNUMBER_DBNAME = "blackNumber.db";
    //创建数据库的版本
    int BLACKNUMBER_DBVERSION = 1;
    //表名
    String BLACKNUMBER_TABLENAME = "blackNumberData";
    //id
    String BLACKNUMBER_ID = "_id";
    //黑名单号码字段
    String BLACKNUMBER_NUMBER = "blacknumber";
    //拦截模式
    String BLACKNUMBER_MODE = "mode";
    //创建表sql语句
    String BLACKNUMBER_CREATETABLESQL = "create table "
            + BLACKNUMBER_TABLENAME + "(" + BLACKNUMBER_ID
            + " integer primary key autoincrement," + BLACKNUMBER_NUMBER
            + " varchar(20)," + BLACKNUMBER_MODE + " varchar(2))";
    //拦截电话号码
    int BLACKNUMBER_CALL = 0;
    //拦截短信
    int BACKNUMBER_SMS = 1;
    //拦截全部
    int BALCKNUMBER_ALL = 2;

}
