package engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.v4.content.ContextCompat;

import com.example.sin.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

import bean.ProcessInfo;

/**
 * Created by Sin on 2016/10/6.
 * Description:
 */

public class ProcessEngine {

    private static String packageName;
    private static long memorySize;
    private static String name;
    private static Drawable icon;

    public static List<ProcessInfo> getRunningProcess(Context context) {
        List<ProcessInfo> list = new ArrayList<>();
        //获取进程的管理者
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //获取正在运行的进程的信息
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processes) {
            ProcessInfo processInfo = new ProcessInfo();
            //包名
            packageName = runningAppProcessInfo.processName;
            processInfo.packageName = packageName;
            //占用内存
            //ActivityManager.MemoryInfo[] memoryInfo=am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            memorySize = memoryInfo[0].getTotalPss() * 1024;
            processInfo.memorySize = memorySize;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                //名称
                name = applicationInfo.loadLabel(pm).toString();
                processInfo.name = name;
                //图标
                icon = applicationInfo.loadIcon(pm);
                processInfo.icon = icon;
                //是否是系统进程
                boolean isSystem;
                int flags = applicationInfo.flags;
                if ((ApplicationInfo.FLAG_SYSTEM & flags) == ApplicationInfo.FLAG_SYSTEM) {
                    isSystem = true;
                } else {
                    isSystem = false;
                }
                processInfo.isSystem = isSystem;
            } catch (PackageManager.NameNotFoundException e) {
                //找不到applicationInfo,表示进程是系统进程
                processInfo.name = packageName;
                // processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);  getDrawable过时
                processInfo.icon = ContextCompat.getDrawable(context, R.drawable.ic_launcher);
                processInfo.isSystem = true;
            }
            list.add(processInfo);
        }
        return list;
    }
}
