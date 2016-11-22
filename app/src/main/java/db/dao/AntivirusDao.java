package db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by Sin on 2016/10/16.
 * Description:
 */

public class AntivirusDao {
    public static boolean isAntivirus(Context context, String md5) {
        boolean isAntivirus = false;
        File file = new File(context.getFilesDir(), "antivirus.db");
        if (file.exists() && !file.isDirectory()) {
            SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = database.query("datable", null, "md5=?", new String[]{md5}, null, null, null);
            if (cursor.moveToNext()) {
                isAntivirus = true;
            }
            cursor.close();
            database.close();
        }
        return isAntivirus;
    }
}
