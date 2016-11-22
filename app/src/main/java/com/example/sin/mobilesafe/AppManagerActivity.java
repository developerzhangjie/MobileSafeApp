package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;
import engine.AppEngine;
import ui.ProgressBarView;

/**
 * Created by Sin on 2016/9/29.
 * Description:软件管家
 */

public class AppManagerActivity extends Activity implements View.OnClickListener {
    private ProgressBarView pv_appmanager_memory;
    private ProgressBarView pv_appmanager_sd;
    private ListView lv_appmanager_applications;
    private LinearLayout ll_appmanager_loading;
    private TextView tv_appmanager_count;
    private List<AppInfo> list;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private AppInfo appInfo;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        initView();
        setMemorySpace();
        setSDcardSpace();
        fillData();
        //TextView浮动显示
        setListViewOnScrollListener();
        //设置popupWindow
        setListViewOnItemClickListener();
    }

    private void initView() {
        pv_appmanager_memory = (ProgressBarView) findViewById(R.id.pv_appmanager_memory);
        pv_appmanager_sd = (ProgressBarView) findViewById(R.id.pv_appmanager_sd);
        lv_appmanager_applications = (ListView) findViewById(R.id.lv_appmanager_applications);
        ll_appmanager_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        tv_appmanager_count = (TextView) findViewById(R.id.tv_appmanager_count);
    }

    //获取内存空间
    private void setMemorySpace() {
        //获取内存路径
        File file = Environment.getDataDirectory();
        //获取可用内存空间,单位是B
        long freeSpace = file.getFreeSpace();
        //获取总内存大小,单位是B
        long totalSpace = file.getTotalSpace();
        //获取已用内存大小,单位是B
        long usedSpace = totalSpace - freeSpace;
        //获取已用内存占总内存的比例
        int usedSpaceRate = (int) (usedSpace * 100f / totalSpace + 0.5f);
        //将B转化成MB
        String freeSize = Formatter.formatFileSize(AppManagerActivity.this, freeSpace);
        String usedSize = Formatter.formatFileSize(AppManagerActivity.this, usedSpace);
        //设置显示
        pv_appmanager_memory.setTitle("内存:");
        pv_appmanager_memory.setFree(freeSize);
        pv_appmanager_memory.setUsed(usedSize);
        pv_appmanager_memory.setProgress(usedSpaceRate);
    }

    private void setSDcardSpace() {
        //获取SD卡路径
        File file = Environment.getExternalStorageDirectory();
        //获取可用SD空间,单位是B
        long freeSpace = file.getFreeSpace();
        //获取总SD大小,单位是B
        long totalSpace = file.getTotalSpace();
        //获取已用SD大小,单位是B
        long usedSpace = totalSpace - freeSpace;
        //获取已用SD占总内存的比例
        int usedSpaceRate = (int) (usedSpace * 100f / totalSpace + 0.5f);
        //将B转化成MB
        String freeSize = Formatter.formatFileSize(AppManagerActivity.this, freeSpace);
        String usedSize = Formatter.formatFileSize(AppManagerActivity.this, usedSpace);
        //设置显示
        pv_appmanager_sd.setTitle("SD:");
        pv_appmanager_sd.setFree(freeSize);
        pv_appmanager_sd.setUsed(usedSize);
        pv_appmanager_sd.setProgress(usedSpaceRate);
    }


    //获取数据
    private void fillData() {
        list = AppEngine.getApplicationsInfo(AppManagerActivity.this);
        userAppInfos = new ArrayList<>();
        systemAppInfos = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                //判断是系统程序还是用户程序
                for (AppInfo appInfo : list
                        ) {
                    if (appInfo.isSystem) {
                        systemAppInfos.add(appInfo);
                    } else {
                        userAppInfos.add(appInfo);
                    }
                }
                super.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示条目
                        tv_appmanager_count.setText("用户程序(" + userAppInfos.size() + ")个");
                        //显示数据
                        lv_appmanager_applications.setAdapter(new MyAdapter());
                        //隐藏加载进度条
                        ll_appmanager_loading.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
    }

    private void setListViewOnScrollListener() {
        lv_appmanager_applications.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupWindowHide();
                //防止空指针异常，加下面的判断
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem >= userAppInfos.size() + 1) {
                        tv_appmanager_count.setText("系统程序(" + systemAppInfos.size() + ")个");
                    } else {
                        tv_appmanager_count.setText("用户程序(" + userAppInfos.size() + ")个");
                    }
                }
            }
        });
    }

    private void setListViewOnItemClickListener() {
        lv_appmanager_applications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //屏蔽TextView操作
                if (position == 0 || position == userAppInfos.size() + 1) {
                    return;
                }
                //获取相应的数据
                if (position <= userAppInfos.size()) {
                    appInfo = userAppInfos.get(position - 1);
                } else {
                    appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
                }
                //显示气泡
                View contentView = View.inflate(AppManagerActivity.this, R.layout.popuwindow_item, null);
                // 初始化控件
                contentView.findViewById(R.id.ll_pop_uninstall).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.ll_pop_open).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.ll_pop_share).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.ll_pop_info).setOnClickListener(AppManagerActivity.this);
                mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //mPopupWindow.setAnimationStyle(R.style.popupWindow);
                mPopupWindow.showAsDropDown(view, 0 + 60, -view.getHeight());
            }
        });

    }

    // 隐藏popuwindow
    private void popupWindowHide() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pop_uninstall:
                uninstallApp();
                break;
            case R.id.ll_pop_open:
                openApp();
                break;
            case R.id.ll_pop_share:
                shareApp();
                break;
            case R.id.ll_pop_info:
                infoApp();
                break;
        }
        popupWindowHide();
    }

    private void uninstallApp() {
        if (!appInfo.packageName.equals(getPackageName())) {
            if (!appInfo.isSystem) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + appInfo.packageName));
                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(AppManagerActivity.this, "禁止卸载系统程序", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AppManagerActivity.this, "禁止卸载程序本身", Toast.LENGTH_SHORT).show();
        }
    }

    //刷新数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();
    }

    //打开应用
    private void openApp() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(AppManagerActivity.this, "系统核心程序，无法打开", Toast.LENGTH_SHORT).show();
        }
    }

    //应用信息
    private void infoApp() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.packageName));
        startActivity(intent);
    }

    //分享应用
    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "发现一个很牛X软件:" + appInfo.name + "  下载地址:www.baidu.com,自己去搜..");
        startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return systemAppInfos.size() + userAppInfos.size();
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
            //两个TextView说明是系统程序还是用户程序
            if (position == 0) {
                // 添加用户程序多少个
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + userAppInfos.size() + "个)");
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                // 添加系统程序多少个
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序(" + systemAppInfos.size() + "个)");
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
                return textView;
            }
            View view;
            ViewHolder viewHolder;
            //convertView instanceof TextView 防止复用缓存的时候发生空指针异常
            if (convertView == null || convertView instanceof TextView) {
                view = View.inflate(AppManagerActivity.this, R.layout.appmanager_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_appmanageritem_icon = (ImageView) view.findViewById(R.id.iv_appmanageritem_icon);
                viewHolder.tv_appmanageritem_name = (TextView) view.findViewById(R.id.tv_appmanageritem_name);
                viewHolder.tv_appmanageritem_issd = (TextView) view.findViewById(R.id.tv_appmanageritem_issd);
                viewHolder.tv_appmanageritem_memory = (TextView) view.findViewById(R.id.tv_appmanageritem_memory);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            //获取相应的数据
            if (position <= userAppInfos.size()) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }//获取相应的数据
            if (position <= userAppInfos.size()) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }
            viewHolder.iv_appmanageritem_icon.setImageDrawable(appInfo.icon);
            viewHolder.tv_appmanageritem_name.setText(appInfo.name);
            viewHolder.tv_appmanageritem_memory.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.memorySize));
            viewHolder.tv_appmanageritem_issd.setText(appInfo.isSD ? "SD卡" : "手机内存");
            return view;
        }
    }

    private static class ViewHolder {
        ImageView iv_appmanageritem_icon;
        TextView tv_appmanageritem_name, tv_appmanageritem_issd, tv_appmanageritem_memory;
    }
}
