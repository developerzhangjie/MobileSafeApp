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
 * Created by Sin on 2016/10/12.
 * Description:
 */

public class SMSOperation {
    private static ContentResolver contentResolver;
    private static Cursor cursor;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String json;
    private String[] elements;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
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

    public static void backupSMS(Context context) {
        contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        cursor = contentResolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        String str = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                str += cursor.getString(0) + "\t";
                str += cursor.getString(1) + "\t";
                str += cursor.getString(2) + "\t";
                str += cursor.getString(3) + "\t";
            }
            try {
                FileWriter fileWriter = new FileWriter(new File("/storage/emulated/0/Android/data/com.example.sin.mobilesafe/cache/shit.txt"));
                fileWriter.write(str);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
