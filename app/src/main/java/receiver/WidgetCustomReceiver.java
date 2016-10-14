package receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Sin on 2016/10/7.
 * Description:
 */

public class WidgetCustomReceiver extends BroadcastReceiver {

    private List<ActivityManager.RunningAppProcessInfo> mRunningAppProcesses;

    @Override
    public void onReceive(Context context, Intent intent) {
        killProcess(context);
    }

    //清理进程(清理缓存)
    private void killProcess(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mRunningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningProcessInfo : mRunningAppProcesses) {
            if (!context.getPackageName().equals(runningProcessInfo)) {
                am.killBackgroundProcesses(runningProcessInfo.processName);
            }
        }
    }
}
