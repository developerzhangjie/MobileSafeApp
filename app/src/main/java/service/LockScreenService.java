package service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Sin on 2016/10/7.
 * Description:
 */

public class LockScreenService extends Service {

    private LockScreenReceiver mLockScreenReceiver;
    private List<ActivityManager.RunningAppProcessInfo> mRunningAppProcesses;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //1.生成广播接收者
        mLockScreenReceiver = new LockScreenReceiver();
        //2.设置过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //3.注册广播接收者
        registerReceiver(mLockScreenReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLockScreenReceiver);
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //进程清理
            killProcess();

        }
    }

    //实现锁屏自动清理操作
    private void killProcess() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mRunningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningProcessInfo : mRunningAppProcesses) {
            if (!getPackageName().equals(runningProcessInfo)) {
                am.killBackgroundProcesses(runningProcessInfo.processName);
            }
        }
    }
}
