package db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by Sin on 2016/9/26.
 * Description:
 */

public class AddressDao {
    public static String getAddress(Context context, String number) {
        String location = "北京";
        File file = new File(context.getFilesDir(), "address.db");
        if (file.exists() && !file.isDirectory()) {
            SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            if (number.matches("^1[34578]\\d{9}$")) {
                //查询数据库的操作
                Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)",
                        new String[]{number.substring(0, 7)});
                //解析cursor
                if (cursor.moveToNext()) {
                    location = cursor.getString(0);
                }
                cursor.close();
            }else{
                switch (number.length()) {
                    case 3:// 110  120
                        location = "紧急电话";
                        break;
                    case 4://5556  5554
                        location = "虚拟电话";
                        break;
                    case 5://10086  10010  10000
                        location = "客服电话";
                        break;
                    case 6:
                        location = "火星来电";
                        break;
                    case 7://本地电话
                    case 8://本地电话
                        location = "本地电话";
                        break;
                    default:
                        //010 1234567 10位      010 12345678  11位      0372 12345678 12位  长途电话
                        //startsWith : 字符串是否以0开始
                        if (number.length() >= 10 && number.startsWith("0")) {
                            //查询长途电话的号码归属地
                            //根据区号查询长途电话号码归属地
                            //3位     4位
                            //获取一个区号
                            //010   10
                            String area = number.substring(1, 3);
                            //根据区号查询数据库
                            Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{area});
                            if (cursor.moveToNext()) {
                                location = cursor.getString(0);
                                //将数据中的后两位中电信等字样去除
                                location = location.substring(0, location.length()-2);
                                cursor.close();
                            }else{
                                //如果查询3位没有查询出来,直接查询4位
                                //0372   372
                                area = number.substring(1, 4);
                                cursor = database.rawQuery("select location from data2 where area=?", new String[]{area});
                                if (cursor.moveToNext()) {
                                    location = cursor.getString(0);
                                    location = location.substring(0, location.length()-2);
                                    cursor.close();
                                }
                            }
                        }
                        break;
                }
            }
            database.close();
        }
        return location;
    }
}
