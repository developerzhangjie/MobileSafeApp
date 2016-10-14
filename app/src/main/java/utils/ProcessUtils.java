package utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Sin on 2016/10/6.
 * Description:
 */

public class ProcessUtils {

    private static List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
    private static PackageManager packageManager;
    private static List<PackageInfo> installedPackages;
    private static long availMem;
    private static long totalMem;

    //获取正在运行的进程数
    public static int getRunningProcessCount(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        runningAppProcesses = activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    //获取总的进程数
    public static int getAllProcessCount(Context context) {
        packageManager = context.getPackageManager();
        //获取系统中所有安装应用的信息
        installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS);
        int count = 0;
        for (PackageInfo packageInfo : installedPackages) {
            //去重
            HashSet<String> hashSet = new HashSet();
            hashSet.add(packageInfo.applicationInfo.processName);

            ActivityInfo[] activities = packageInfo.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    hashSet.add(activityInfo.processName);
                }
            }

            ServiceInfo[] services = packageInfo.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    hashSet.add(serviceInfo.processName);
                }
            }

            ActivityInfo[] receivers = packageInfo.receivers;
            if (receivers != null) {
                for (ActivityInfo receiverInfo : receivers) {
                    hashSet.add(receiverInfo.processName);
                }
            }

            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    hashSet.add(providerInfo.processName);
                }
            }
            count += hashSet.size();
        }
        return count;
    }

    //获取可用内存
    public static long getFreeMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //创建白纸
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //将内存信息写入白纸
        am.getMemoryInfo(memoryInfo);
        //获取可用内存
        availMem = memoryInfo.availMem;
        return availMem;
    }

    //获取总内存
    @SuppressLint("NewApi")
    public static long getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //创建白纸
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //将内存信息写入白纸
        am.getMemoryInfo(memoryInfo);
        //获取可用内存
        if (Build.VERSION.SDK_INT >= 16) {
            totalMem = memoryInfo.totalMem;
        } else {
            totalMem = getProcTotalMemory();
        }
        return totalMem;
    }

    private static long getProcTotalMemory() {
        File file = new File("proc/meminfo");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readLine = br.readLine();
            readLine.replace("MemTotal:", "");
            readLine.replace("kB", "");
            String trim = readLine.trim();
            long memory = Long.parseLong(trim);
            return memory * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
