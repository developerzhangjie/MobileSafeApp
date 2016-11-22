package engine;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Sin on 2016/10/11.
 * Description:获取系统短信
 */

public class SMSEngine {
    private static ContentResolver contentResolver;
    private static Cursor cursor;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void getSMS(Context context, ShowProgress showProgress) {
        contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        cursor = contentResolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        //设置最大进度
        showProgress.setMax(cursor.getCount());
        int progress = 0;
        String str = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // \t是用来恢复短信时分割的，手机无法输入制表符
                str += cursor.getString(0) + "\t";
                str += cursor.getString(1) + "\t";
                str += cursor.getString(2) + "\t";
                str += cursor.getString(3) + "\t";
                progress++;
                showProgress.setProgress(progress);
            }
            try {
                FileWriter fileWriter = new FileWriter(new File
                        ("/storage/emulated/0/Android/data/com.example.sin.mobilesafe/cache/sms_backup.txt"));
                fileWriter.write(str);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //1.创建刷子
    public interface ShowProgress {
        void setMax(int max);

        void setProgress(int progress);
    }
}



