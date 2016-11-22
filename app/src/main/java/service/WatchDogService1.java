package service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.sin.mobilesafe.WatchDogActivity;

import java.util.List;

import db.dao.WatchDogDao;

/**
 * Created by Sin on 2016/10/11.
 * Description:
 */

public class WatchDogService1 extends Service {
    private boolean isWatch = true;
    private ComponentName mBaseActivity;
    private WatchDogDao mWatchDogDao;
    private CurrentUnlockAppReceiver mCurrentUnlockAppReceiver;
    private LockScreenOffReceiver mLockScreenOffReceiver;
    private String mPackageName;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentUnlockAppReceiver = new CurrentUnlockAppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("finishActivity");
        registerReceiver(mCurrentUnlockAppReceiver, intentFilter);
        mLockScreenOffReceiver = new LockScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mLockScreenOffReceiver, filter);
        new Thread() {
            @Override
            public void run() {
                super.run();
                mWatchDogDao = new WatchDogDao(WatchDogService1.this);
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                while (isWatch) {
                    String packName; /* Android5.0之后获取程序锁的方式是不一样的*/
                    if (Build.VERSION.SDK_INT > 20) {
                        // 5.0及其以后的版本
                        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                        while (null != tasks && tasks.size() > 0) {
                            packName = tasks.get(0).processName;
                            //判断有没有加锁
                            if (mWatchDogDao.queryLockAPP(packName)) {
                                //解决一个按home键产生的bug
                                if (!packName.equals(mPackageName)) {
                                    Intent intent = new Intent(WatchDogService1.this, WatchDogActivity.class);
                                    //从服务中启动活动需要加上这句
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("packageName", packName);
                                    startActivity(intent);
                                }
                            }
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // 5.0之前
                        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                        for (ActivityManager.RunningTaskInfo runningTaskInfo : infos) {
                            mBaseActivity = runningTaskInfo.baseActivity;
                            packName = mBaseActivity.getPackageName();
                            //判断程序是否加锁，加锁程序，弹出加锁界面
                            if (mWatchDogDao.queryLockAPP(packName)) {
                                if (!packName.equals(mPackageName)) {
                                    Intent intent = new Intent(WatchDogService1.this, WatchDogActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("packageName", packName);
                                    startActivity(intent);
                                }
                            }
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWatch = false;
        unregisterReceiver(mCurrentUnlockAppReceiver);
        unregisterReceiver(mLockScreenOffReceiver);
    }

    public class CurrentUnlockAppReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPackageName = intent.getStringExtra("packageName");
        }
    }

    public class LockScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPackageName = null;
        }
    }
}
