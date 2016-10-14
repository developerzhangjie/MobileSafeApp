package service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.sin.mobilesafe.R;

import java.util.Timer;
import java.util.TimerTask;

import receiver.WidgetReceiver;
import utils.ProcessUtils;

/**
 * Created by Sin on 2016/10/7.
 * Description:小部件的服务
 */

public class WidgetService extends Service {

    private AppWidgetManager mAppWidgetManager;
    private String mFreeMemory;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        updateWidget();

    }

    private void updateWidget() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ComponentName componentName = new ComponentName(WidgetService.this, WidgetReceiver.class);
                RemoteViews remoteViews = new RemoteViews(WidgetService.this.getPackageName(), R.layout.process_widget);
                //更新远程布局显示的内容
                int runningAppCount = ProcessUtils.getRunningProcessCount(WidgetService.this);
                remoteViews.setTextViewText(R.id.process_count, "正在运行的软件：" + runningAppCount + "个");
                mFreeMemory = Formatter.formatFileSize(WidgetService.this, ProcessUtils.getFreeMemory(WidgetService.this));
                remoteViews.setTextViewText(R.id.process_memory, "可用内存" + mFreeMemory);
                //设置点击事件
                Intent intent = new Intent();
                intent.setAction("CLEAR_CACHE");//自定义广播
                PendingIntent pendingIntent = PendingIntent.getActivity(WidgetService.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                mAppWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        };
        timer.schedule(timerTask, 2000, 2000);
    }
}
