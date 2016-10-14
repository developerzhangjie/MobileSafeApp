package engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;

/**
 * Created by Sin on 2016/9/29.
 * Description:获取应用程序的信息
 */

public class AppEngine {

    public static List<AppInfo> getApplicationsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            //包名
            String packageName = packageInfo.packageName;
            //图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            //名称
            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            //占用空间大小
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            long footprint = new File(sourceDir).length();
            //获取是否是系统应用
            boolean isSystemApp;
            int flagSystem = packageInfo.applicationInfo.flags;
            if ((flagSystem & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                isSystemApp = true;
            } else {
                isSystemApp = false;
            }
            //获取应用是否安装到sd卡中
            boolean isSDApp;
            int flagsSD = packageInfo.applicationInfo.flags;
            if ((flagsSD & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                isSDApp = true;
            } else {
                isSDApp = false;
            }
            //获取uid
            int uid=packageInfo.applicationInfo.uid;
            AppInfo appInfo = new AppInfo(icon, isSDApp, isSystemApp, footprint, name, packageName,uid);
            list.add(appInfo);
        }
        return list;
    }
}
