package com.example.sin.mobilesafe;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bean.AppInfo;
import engine.AppEngine;

/**
 * Created by Sin on 2016/10/13.
 * Description:
 */

public class TrafficManagerActivity extends Activity {
    private ListView lv_traffic_application;
    private LinearLayout ll_traffic_loading;
    private List<AppInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficmanager);
        initView();
    }

    private void initView() {
        lv_traffic_application = (ListView) findViewById(R.id.lv_traffic_application);
        ll_traffic_loading = (LinearLayout) findViewById(R.id.ll_traffic_loading);
        list = AppEngine.getApplicationsInfo(TrafficManagerActivity.this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                lv_traffic_application.setAdapter(new MyAdapter());
                ll_traffic_loading.setVisibility(View.GONE);
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {

        private AppInfo mAppInfo;
        private long mUidTxBytes;
        private String mTxSize;
        private long mUidRxBytes;
        private String mRxSize;

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
            View view = View.inflate(TrafficManagerActivity.this, R.layout.traffic_item, null);
            ImageView iv_trffic_icon = (ImageView) view.findViewById(R.id.iv_trffic_icon);
            TextView tv_traffic_title = (TextView) view.findViewById(R.id.tv_traffic_title);
            TextView tv_traffic_desc = (TextView) view.findViewById(R.id.tv_traffic_desc);
            TextView tv_traffic_desc1 = (TextView) view.findViewById(R.id.tv_traffic_desc1);
            //获取展示数据
            mAppInfo = list.get(position);
            iv_trffic_icon.setImageDrawable(mAppInfo.icon);
            tv_traffic_title.setText(mAppInfo.name);
            //获取上传流量
            mUidTxBytes = TrafficStats.getUidTxBytes(mAppInfo.uid);
            mTxSize = Formatter.formatFileSize(TrafficManagerActivity.this, mUidTxBytes);
            tv_traffic_desc.setText("上传：" + mTxSize);
            //获取下载流量
            mUidRxBytes = TrafficStats.getUidRxBytes(mAppInfo.uid);
            mRxSize = Formatter.formatFileSize(TrafficManagerActivity.this, mUidRxBytes);
            tv_traffic_desc1.setText("下载：" + mRxSize);
            return view;
        }
    }
}
