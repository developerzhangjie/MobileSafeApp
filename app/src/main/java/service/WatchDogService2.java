package service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import com.example.sin.mobilesafe.WatchDogActivity;

import db.dao.WatchDogDao;

/**
 * Created by Sin on 2016/10/11.
 * Description:
 */

public class WatchDogService2 extends AccessibilityService {

    private int mEventType;
    private String packageName;
    private WatchDogDao mWatchDogDao;
    private String mPackageName;
    private WatchDogService2.CurrentUnlockAppReceiver mCurrentUnlockAppReceiver;
    private WatchDogService2.LockScreenOffReceiver mLockScreenOffReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchDogDao = new WatchDogDao(this);
        mCurrentUnlockAppReceiver = new WatchDogService2.CurrentUnlockAppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("finishActivity");
        registerReceiver(mCurrentUnlockAppReceiver, intentFilter);
        mLockScreenOffReceiver = new WatchDogService2.LockScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mLockScreenOffReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mCurrentUnlockAppReceiver);
        unregisterReceiver(mLockScreenOffReceiver);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mEventType = event.getEventType();
        if (mEventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            packageName = event.getPackageName().toString();
            if (mWatchDogDao.queryLockAPP(packageName)) {
                //解决一个按home键产生的bug
                if (!packageName.equals(mPackageName)) {
                    Intent intent = new Intent(WatchDogService2.this, WatchDogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("packageName", packageName);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

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
