package com.example.sin.mobilesafe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;
import db.dao.WatchDogDao;
import engine.AppEngine;

/**
 * Created by Sin on 2016/10/8.
 * Description:程序锁
 */

public class AppLockActivity extends Activity implements View.OnClickListener {
    private Button bn_applock_unlock;
    private Button bn_applock_lock;
    private TextView tv_applock_lock;
    private TextView tv_applock_unlock;
    private ListView lv_applock_lock;
    private ListView lv_applock_unlock;
    private LinearLayout ll_applock_lock;
    private LinearLayout ll_applock_unlock;
    private List<AppInfo> mApplicationsInfo;
    private List<AppInfo> locklist;
    private List<AppInfo> unlocklist;
    private WatchDogDao mWatchDogDao;
    private MyAdapter lockAdapter;
    private MyAdapter unlockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        initView();
        bn_applock_unlock.setOnClickListener(this);
        bn_applock_lock.setOnClickListener(this);
    }

    private void initView() {
        bn_applock_unlock = (Button) findViewById(R.id.bn_applock_unlock);
        bn_applock_lock = (Button) findViewById(R.id.bn_applock_lock);
        tv_applock_lock = (TextView) findViewById(R.id.tv_applock_lock);
        tv_applock_unlock = (TextView) findViewById(R.id.tv_applock_unlock);
        lv_applock_lock = (ListView) findViewById(R.id.lv_applock_lock);
        lv_applock_unlock = (ListView) findViewById(R.id.lv_applock_unlock);
        ll_applock_lock = (LinearLayout) findViewById(R.id.ll_applock_lock);
        ll_applock_unlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
        mWatchDogDao = new WatchDogDao(AppLockActivity.this);
        new Thread() {
            @Override
            public void run() {
                //获取系统中的所有安装程序
                mApplicationsInfo = AppEngine.getApplicationsInfo(AppLockActivity.this);
                locklist = new ArrayList<>();
                unlocklist = new ArrayList<>();
                for (AppInfo appInfo : mApplicationsInfo) {
                    //判断程序是否加锁，加锁存储到加锁的列表，未加锁存储到未加锁的列表
                    if (mWatchDogDao.queryLockAPP(appInfo.packageName)) {
                        //加锁
                        locklist.add(appInfo);
                    } else {
                        //未加锁
                        unlocklist.add(appInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示数据
                        lockAdapter = new MyAdapter(true);
                        lv_applock_lock.setAdapter(lockAdapter);
                        unlockAdapter = new MyAdapter(false);
                        lv_applock_unlock.setAdapter(unlockAdapter);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bn_applock_unlock:
                //点击未加锁
                //图片背景更换
                bn_applock_unlock.setBackgroundResource(R.drawable.dg_btn_confirm_normal);
                bn_applock_lock.setBackgroundResource(R.drawable.dg_button_cancel_normal);
                //文字颜色更换
                bn_applock_unlock.setTextColor(Color.parseColor("#ffffff"));
                bn_applock_lock.setTextColor(Color.parseColor("#429ED6"));
                //列表界面更换
                ll_applock_unlock.setVisibility(View.VISIBLE);
                ll_applock_lock.setVisibility(View.GONE);
                break;
            case R.id.bn_applock_lock:
                //点击加锁
                bn_applock_lock.setBackgroundResource(R.drawable.dg_btn_confirm_normal);
                bn_applock_unlock.setBackgroundResource(R.drawable.dg_button_cancel_normal);
                bn_applock_lock.setTextColor(Color.parseColor("#ffffff"));
                bn_applock_unlock.setTextColor(Color.parseColor("#429ED6"));
                ll_applock_lock.setVisibility(View.VISIBLE);
                ll_applock_unlock.setVisibility(View.GONE);
                break;
        }
    }

    public class MyAdapter extends BaseAdapter {
        private boolean islock;
        private final TranslateAnimation right;
        private final TranslateAnimation left;

        public MyAdapter(boolean islock) {
            this.islock = islock;
            //创建动画
            right = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            left = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            right.setDuration(400);
            left.setDuration(400);
        }

        @Override
        public int getCount() {
            //显示加锁和未加锁程序个数
            showCount();
            if (islock) {
                return locklist.size();
            } else {
                return unlocklist.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (islock) {
                return locklist.get(position);
            } else {
                return unlocklist.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.applock_item, null);
                viewHolder.iv_applockitem_icon = (ImageView) view.findViewById(R.id.iv_applockitem_icon);
                viewHolder.iv_applockitem_islock = (ImageView) view.findViewById(R.id.iv_applockitem_islock);
                viewHolder.tv_applockitem_name = (TextView) view.findViewById(R.id.tv_applockitem_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            //获取数据
            final AppInfo appInfo = (AppInfo) getItem(position);
            viewHolder.iv_applockitem_icon.setImageDrawable(appInfo.icon);
            viewHolder.tv_applockitem_name.setText(appInfo.name);
            // 加锁列表显示解锁图标,解锁列表显示加锁图标
            if (islock) {
                viewHolder.iv_applockitem_islock.setBackgroundResource(R.drawable.applock_unlock_selector);
            } else {
                viewHolder.iv_applockitem_islock.setBackgroundResource(R.drawable.applock_lock_selector);
            }
            viewHolder.iv_applockitem_islock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否是当前应用
                    if (appInfo.packageName.equals(getPackageName())) {
                        Toast.makeText(AppLockActivity.this, "不能加锁当前应用", Toast.LENGTH_SHORT).show();
                    } else {
                        if (islock) {
                            view.startAnimation(left);
                            left.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    mWatchDogDao.deleteLockApp(appInfo.packageName);
                                    locklist.remove(appInfo);
                                    unlocklist.add(appInfo);
                                    lockAdapter.notifyDataSetChanged();
                                    unlockAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        } else {
                            view.startAnimation(right);
                            right.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    //程序加锁
                                    mWatchDogDao.addLockAPP(appInfo.packageName);
                                    unlocklist.remove(appInfo);
                                    locklist.add(appInfo);
                                    lockAdapter.notifyDataSetChanged();
                                    unlockAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                    }
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_applockitem_icon, iv_applockitem_islock;
        TextView tv_applockitem_name;
    }

    //设置显示加锁和未加锁个数
    public void showCount() {
        tv_applock_lock.setText("已加锁(" + locklist.size() + ")");
        tv_applock_unlock.setText("未加锁(" + unlocklist.size() + ")");
    }
}
