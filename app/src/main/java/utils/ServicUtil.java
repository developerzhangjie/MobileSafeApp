package utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by Sin on 2016/9/26.
 * Description:判断服务是否正在运行
 */

public class ServicUtil {
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //返回所有正在运行的服务 数目是1000个
        List<ActivityManager.RunningServiceInfo > runningServices = activityManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServices) {
            //获取正在运行的服务
            ComponentName componentName = runningServiceInfo.service;
            String className = componentName.getClassName();
            if (className.equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}
