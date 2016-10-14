package db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2016/9/28.
 * Description:
 */

public class CommonNumberDao {

    public static List<GroupInfo> getGroups(Context context) {
        List<GroupInfo> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String idx = cursor.getString(1);
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.name = name;
                groupInfo.idx = idx;
                groupInfo.child = getChilds(context, idx);
                list.add(groupInfo);
            }
            cursor.close();
        }
        database.close();
        return list;
    }


    public static List<ChildInfo> getChilds(Context context, String idx) {
        List<ChildInfo> list = new ArrayList<>();
        //打开数据库
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("table" + idx, new String[]{"number", "name"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                String name = cursor.getString(1);
                ChildInfo childInfo = new ChildInfo();
                childInfo.number = number;
                childInfo.name = name;
                list.add(childInfo);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public static class GroupInfo {
        public String name;
        public String idx;
        public List<ChildInfo> child;
    }

    public static class ChildInfo {
        public String name;
        public String number;
    }
}
