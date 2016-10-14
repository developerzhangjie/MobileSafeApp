package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import bean.CacheInfo;

/**
 * Created by Sin on 2016/10/13.
 * Description:
 */

public class ClearCacheActivity extends Activity {
    private ImageView iv_clearcache_scanline;
    private Button btn_clearcache_clear;
    private ProgressBar pb_clearcache_porgress;
    private ImageView iv_clearcache_icon;
    private TextView tv_clearcache_name;
    private List<CacheInfo> list;
    private List<PackageInfo> packages;
    private TextView tv_clearcache_clearsize;
    private PackageManager pm;
    private ListView lv_clearcache_applications;
    private MyAdapter mMyAdapter;

    //缓存软件的个数
    private int totalCount = 0;
    //缓存软件的缓存总大小
    private long cachesizecount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clearcache);
        initView();
    }

    private void initView() {
        iv_clearcache_scanline = (ImageView) findViewById(R.id.iv_clearcache_scanline);
        btn_clearcache_clear = (Button) findViewById(R.id.btn_clearcache_clear);
        pb_clearcache_porgress = (ProgressBar) findViewById(R.id.pb_clearcache_porgress);
        iv_clearcache_icon = (ImageView) findViewById(R.id.iv_clearcache_icon);
        tv_clearcache_name = (TextView) findViewById(R.id.tv_clearcache_name);
        tv_clearcache_clearsize = (TextView) findViewById(R.id.tv_clearcache_clearsize);
        lv_clearcache_applications = (ListView) findViewById(R.id.lv_clearcache_applications);

       /* ll_clearcache_progressbar = (LinearLayout) findViewById(ll_clearcache_progressbar);
        rel_clearcache_scan = (RelativeLayout) findViewById(rel_clearcache_scan);
        tv_clearcache_cleartext = (TextView) findViewById(tv_clearcache_cleartext);
        btn_clearcache_scan = (Button) findViewById(btn_clearcache_scan);*/

        scan();
    }

    private void scan() {
        list = new ArrayList<>();
        list.clear();
        //设置图标里面的横线上下滑动的动画
        TranslateAnimation translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0, TranslateAnimation.RELATIVE_TO_PARENT, 0,
                TranslateAnimation.RELATIVE_TO_PARENT, 0, TranslateAnimation.RELATIVE_TO_PARENT, 1);
        translateAnimation.setDuration(200);
        translateAnimation.setRepeatCount(TranslateAnimation.INFINITE);
        translateAnimation.setRepeatMode(TranslateAnimation.REVERSE);
        iv_clearcache_scanline.startAnimation(translateAnimation);
        //屏蔽一键清理的点击事件
        btn_clearcache_clear.setEnabled(false);
        //扫描应用程序设置进度条的进度
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //设置进度条的最大进度
                packages = pm.getInstalledPackages(0);
                pb_clearcache_porgress.setMax(packages.size());
                int progress = 0;
                for (final PackageInfo packageInfo : packages) {
                    //进度条的速度太快
                    SystemClock.sleep(50);
                    progress++;
                    pb_clearcache_porgress.setProgress(progress);
                    //显示应用程序的图标和名称
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ApplicationInfo mApplicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0);
                                String name = mApplicationInfo.loadLabel(pm).toString();
                                Drawable icon = mApplicationInfo.loadIcon(pm);
                                tv_clearcache_name.setText(name);
                                iv_clearcache_icon.setImageDrawable(icon);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //5.获取缓存大小
                    //反射
                    try {
                        //Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        getPackageSizeInfo.invoke(pm, packageInfo.packageName, mStatsObserver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            final long cachesize = stats.cacheSize;
            String packagename = stats.packageName;
            //System.out.println(packagename + "   cachesize:" + cachesize);
            //6.设置显示缓存信息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_clearcache_clearsize.setText("缓存大小：" + Formatter.formatFileSize(ClearCacheActivity.this, cachesize));
                }
            });
            //7.设置显示扫描软件的信息
            try {
                ApplicationInfo mApplicationInfo = pm.getApplicationInfo(packagename, 0);
                String name = mApplicationInfo.loadLabel(pm).toString();
                Drawable icon = mApplicationInfo.loadIcon(pm);
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.name = name;
                cacheInfo.packagename = packagename;
                cacheInfo.icon = icon;
                cacheInfo.cachesize = cachesize;
                list.add(cacheInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //设置adapter
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMyAdapter == null) {
                        mMyAdapter = new MyAdapter();
                        lv_clearcache_applications.setAdapter(mMyAdapter);
                    }
                        mMyAdapter.notifyDataSetChanged();

                    lv_clearcache_applications.smoothScrollToPosition(mMyAdapter.getCount());
                    /*if (list.size() != packages.size()) {
                        lv_clearcache_applications.smoothScrollToPosition(mMyAdapter.getCount());
                    } else {
                        lv_clearcache_applications.smoothScrollToPosition(0);
                    }*/
                   /* if (packages.size() == list.size()) {
                        lv_clearcache_applications.smoothScrollToPosition(0);
                    }*/
                }
            });
        }
    };

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(ClearCacheActivity.this, R.layout.clearcache_item, null);
                viewHolder.iv_clearcache_icon = (ImageView) view.findViewById(R.id.iv_clearcache_icon);
                viewHolder.iv_clearcache_clear = (ImageView) view.findViewById(R.id.iv_clearcache_clear);
                viewHolder.tv_clearcache_title = (TextView) view.findViewById(R.id.tv_clearcache_title);
                viewHolder.tv_clearcache_desc = (TextView) view.findViewById(R.id.tv_clearcache_desc);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            //显示数据
            CacheInfo cacheInfo = list.get(position);
            viewHolder.iv_clearcache_icon.setImageDrawable(cacheInfo.icon);
            viewHolder.tv_clearcache_title.setText(cacheInfo.name);
            viewHolder.tv_clearcache_desc.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), cacheInfo.cachesize));
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_clearcache_icon, iv_clearcache_clear;
        TextView tv_clearcache_title, tv_clearcache_desc;
    }

}



