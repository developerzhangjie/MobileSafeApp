package com.example.sin.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.ProcessInfo;
import engine.ProcessEngine;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import service.LockScreenService;
import ui.ProgressBarView;
import ui.SettingView;
import utils.Constants;
import utils.ProcessUtils;
import utils.ServicUtil;
import utils.SharedPreferencesUtils;

import static android.text.format.Formatter.formatFileSize;

/**
 * Created by Sin on 2016/10/6.
 * Description:
 */

public class ProcessManagerActivity extends Activity implements View.OnClickListener {
    private ProgressBarView pv_processsmanager_processcount;
    private ProgressBarView pv_processsmanager_mermory;
    private LinearLayout ll_processmanager_loading;
    private StickyListHeadersListView lv_processmanager_process;
    private int mRunningProcessCount;
    private int mAllProcessCount;
    private long mFreeMemory;
    private long mTotalMemory;
    private String freeSize;
    private String usedSize;
    private List<ProcessInfo> mList;
    private List<ProcessInfo> userProcessInfos;
    private List<ProcessInfo> systemProcessInfos;
    private MyAdapter mMyAdapter;
    private Button checkInverse;
    private Button checkAll;
    private ImageView iv_processmanager_clear;
    private int mFreeProcessCount;
    private int mRunningProcessRate;
    private int mUsedMemoryRate;
    private SlidingDrawer sd_processmanager_slidingdrawer;
    private LinearLayout ll_processmanager_handle;
    private ImageView iv_processmanager_drawerarrowup1;
    private ImageView iv_processmanager_drawerarrowup2;
    private LinearLayout ll_processmanager_content;
    private SettingView sv_processmanager_isshowsystem;
    private SettingView sv_processmanager_lockscreenclear;
    //系统进程是否显示标示
    private boolean isShowSystem = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmanager);
        initView();
        fillData();
        setListViewOnItemClickListener();
        setViewOnClickListener();

    }

    private void initView() {
        pv_processsmanager_processcount = (ProgressBarView) findViewById(R.id.pv_processsmanager_processcount);
        pv_processsmanager_mermory = (ProgressBarView) findViewById(R.id.pv_processsmanager_mermory);
        ll_processmanager_loading = (LinearLayout) findViewById(R.id.ll_processmanager_loading);
        lv_processmanager_process = (StickyListHeadersListView) findViewById(R.id.lv_processmanager_process);
        iv_processmanager_clear = (ImageView) findViewById(R.id.iv_processmanager_clear);
        sd_processmanager_slidingdrawer = (SlidingDrawer) findViewById(R.id.sd_processmanager_slidingdrawer);
        ll_processmanager_handle = (LinearLayout) findViewById(R.id.ll_processmanager_handle);
        iv_processmanager_drawerarrowup1 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrowup1);
        iv_processmanager_drawerarrowup2 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrowup2);
        ll_processmanager_content = (LinearLayout) findViewById(R.id.ll_processmanager_content);
        sv_processmanager_isshowsystem = (SettingView) findViewById(R.id.sv_processmanager_isshowsystem);
        sv_processmanager_lockscreenclear = (SettingView) findViewById(R.id.sv_processmanager_lockscreenclear);

        checkAll = (Button) findViewById(R.id.checkAll);
        checkInverse = (Button) findViewById(R.id.checkInverse);
        //获取正在运行的进程数
        mRunningProcessCount = ProcessUtils.getRunningProcessCount(ProcessManagerActivity.this);
        showProcessData();
        showMemoryData();
        sd_processmanager_slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                closeAnimation();
            }
        });
        sd_processmanager_slidingdrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                openAnimation();
            }
        });
    }

    //关闭动画
    private void closeAnimation() {
        iv_processmanager_drawerarrowup1.clearAnimation();//清除动画
        iv_processmanager_drawerarrowup2.clearAnimation();
        //更改图片
        iv_processmanager_drawerarrowup1.setImageResource(R.drawable.drawer_arrow_down);
        iv_processmanager_drawerarrowup2.setImageResource(R.drawable.drawer_arrow_down);
    }

    //打开动画
    private void openAnimation() {
        //将图片更改回来
        iv_processmanager_drawerarrowup1.setImageResource(R.drawable.drawer_arrow_up);
        iv_processmanager_drawerarrowup2.setImageResource(R.drawable.drawer_arrow_up);
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.2f, 1.0f);//表示半透明到不透明
        alphaAnimation1.setRepeatCount(Animation.INFINITE);//一直运行
        alphaAnimation1.setRepeatMode(Animation.REVERSE);//设置动画的模式
        alphaAnimation1.setDuration(300);
        iv_processmanager_drawerarrowup1.startAnimation(alphaAnimation1);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.2f);//表示不透明到半透明
        alphaAnimation2.setRepeatCount(Animation.INFINITE);//一直运行
        alphaAnimation2.setRepeatMode(Animation.REVERSE);//设置动画的模式
        alphaAnimation2.setDuration(300);
        iv_processmanager_drawerarrowup2.startAnimation(alphaAnimation2);
    }

    //显示进程数据
    private void showProcessData() {
        //获取总的进程数
        mAllProcessCount = ProcessUtils.getAllProcessCount(ProcessManagerActivity.this);
        //获取空闲的进程数
        mFreeProcessCount = mAllProcessCount - mRunningProcessCount;
        //获取进度条的进度
        mRunningProcessRate = (int) (mRunningProcessCount * 100f / mAllProcessCount + 0.5f);
        //显示数据
        pv_processsmanager_processcount.setTitle("进程数:");
        pv_processsmanager_processcount.setUsed("正在运行" + mRunningProcessCount + "个");
        pv_processsmanager_processcount.setFree("可用进程:" + mFreeProcessCount + "个");
        pv_processsmanager_processcount.setProgress(mRunningProcessRate);
    }

    //显示内存数据
    private void showMemoryData() {
        //获取可用内存
        mFreeMemory = ProcessUtils.getFreeMemory(ProcessManagerActivity.this);
        //获取总内存
        mTotalMemory = ProcessUtils.getTotalMemory(ProcessManagerActivity.this);
        //获取已用内存
        long usedMemory = mTotalMemory - mFreeMemory;
        //获取进度条的进度
        mUsedMemoryRate = (int) (mFreeMemory * 100f / mTotalMemory + 0.5f);
        //单位转换
        freeSize = formatFileSize(ProcessManagerActivity.this, mFreeMemory);
        usedSize = formatFileSize(ProcessManagerActivity.this, usedMemory);
        //显示数据
        pv_processsmanager_mermory.setTitle("内存:   ");
        pv_processsmanager_mermory.setUsed("占用内存:" + usedSize);
        pv_processsmanager_mermory.setFree("可用内存:" + freeSize);
        pv_processsmanager_mermory.setProgress(mUsedMemoryRate);
    }

    //回显操作
    @Override
    protected void onStart() {
        super.onStart();
        //系统进程是否显示的回显操作
        boolean b = SharedPreferencesUtils.getBoolean(ProcessManagerActivity.this, Constants.PROCESSISSHOWSYSTEM, true);
        sv_processmanager_isshowsystem.setToggle(b);
        //根据标示设置系统进程是否显示
        isShowSystem = b;
        //锁屏自动清理的回显操作
        if (ServicUtil.isServiceRunning(getApplicationContext(), "service.LockScreenService")) {
            sv_processmanager_lockscreenclear.setToggle(true);
        } else {
            sv_processmanager_lockscreenclear.setToggle(false);
        }
    }

    private void setViewOnClickListener() {
        checkAll.setOnClickListener(this);
        checkInverse.setOnClickListener(this);
        iv_processmanager_clear.setOnClickListener(this);
        sv_processmanager_isshowsystem.setOnClickListener(this);
        sv_processmanager_lockscreenclear.setOnClickListener(this);
    }

    private void setListViewOnItemClickListener() {
        lv_processmanager_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.获取点击条目对性的数据
                ProcessInfo processInfo;
                if (position <= userProcessInfos.size() - 1) {
                    processInfo = userProcessInfos.get(position);
                } else {
                    processInfo = systemProcessInfos.get(position - userProcessInfos.size());
                }
                //2.修改chexkBox的状态
                if (processInfo.isChecked) {
                    processInfo.isChecked = false;
                } else {
                    processInfo.isChecked = true;
                }
                //3.更新界面
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fillData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mList = ProcessEngine.getRunningProcess(ProcessManagerActivity.this);
                userProcessInfos = new ArrayList<>();
                systemProcessInfos = new ArrayList<>();
                //拆分是系统进程还是用户进程
                for (ProcessInfo processInfo : mList) {
                    if (processInfo.isSystem) {
                        systemProcessInfos.add(processInfo);
                    } else {
                        userProcessInfos.add(processInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示数据
                        mMyAdapter = new MyAdapter();
                        lv_processmanager_process.setAdapter(mMyAdapter);
                        //数据显示完成之后，隐藏进度条
                        ll_processmanager_loading.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
    }

    //全选 反选 清理缓存 的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全选
            case R.id.checkAll:
                //将所有进程的checkBox状态置为true
                //用户进程
                for (ProcessInfo processInfo : userProcessInfos) {
                    if (!processInfo.packageName.equals(getPackageName())) {
                        processInfo.isChecked = true;
                    }
                }
                //系统进程
                //根据系统进程是否显示的表示来进行全选
                if (isShowSystem) {
                    for (ProcessInfo processInfo : systemProcessInfos) {
                        processInfo.isChecked = true;
                    }
                }
                //更新界面
                mMyAdapter.notifyDataSetChanged();
                break;
            //反选
            case R.id.checkInverse:
                for (ProcessInfo processInfo : userProcessInfos) {
                    if (!processInfo.packageName.equals(getPackageName())) {
                        processInfo.isChecked = !processInfo.isChecked;
                        /*
                        * 上面一行等价于
                        * if(processInfo.isChecked = true){
                        *   processInfo.isChecked = false;
                        * }else {
                        *   processInfo.isChecked = true;
                        * }
                        * */
                    }
                }
                //系统进程
                if (isShowSystem) {
                    for (ProcessInfo processInfo : systemProcessInfos) {
                        processInfo.isChecked = !processInfo.isChecked;
                    }
                }
                //更新界面
                mMyAdapter.notifyDataSetChanged();
                break;
            //清理进程
            case R.id.iv_processmanager_clear:
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //保存杀死的进程的信息
                List<ProcessInfo> killedPrecess = new ArrayList<>();
                //用户进程
                for (ProcessInfo processInfo : userProcessInfos) {
                    if (processInfo.isChecked) {
                        am.killBackgroundProcesses(processInfo.packageName);
                        killedPrecess.add(processInfo);
                    }
                }
                //系统进程
                if (isShowSystem) {
                    for (ProcessInfo processInfo : systemProcessInfos) {
                        if (processInfo.isChecked) {
                            am.killBackgroundProcesses(processInfo.packageName);
                            killedPrecess.add(processInfo);
                        }
                    }
                }
                //更新界面
                long memorySize = 0;
                for (ProcessInfo processInfo : killedPrecess) {
                    if (processInfo.isSystem) {
                        systemProcessInfos.remove(processInfo);
                    } else {
                        userProcessInfos.remove(processInfo);
                    }
                    memorySize = +processInfo.memorySize;
                }
                String memSize = Formatter.formatFileSize(getApplicationContext(), memorySize);
                Toast.makeText(getApplicationContext(), "清理" + killedPrecess.size() + "个进程,释放" + memSize + "内存", Toast.LENGTH_SHORT).show();
                //获取清理之后的进程数
                mRunningProcessCount = mRunningProcessCount - killedPrecess.size();
                //刷新进程显示
                showProcessData();
                //刷新内存显示
                showMemoryData();
                mMyAdapter.notifyDataSetChanged();
                break;
            //设置系统进程是否显示
            case R.id.sv_processmanager_isshowsystem:
                sv_processmanager_isshowsystem.toggle();
                boolean toggle = sv_processmanager_isshowsystem.getToggle();
                isShowSystem = toggle;
                //更新界面
                mMyAdapter.notifyDataSetChanged();
                SharedPreferencesUtils.saveBoolean(ProcessManagerActivity.this, Constants.PROCESSISSHOWSYSTEM, sv_processmanager_isshowsystem.getToggle());
                break;
            //设置锁屏自动清理
            case R.id.sv_processmanager_lockscreenclear:
                Intent intent = new Intent(ProcessManagerActivity.this, LockScreenService.class);
                //关闭或者打开服务
                //关闭状态 点击->打开  打开状态 点击 ——>关闭
                if (ServicUtil.isServiceRunning(getApplicationContext(), "service.LockScreenService")) {
                    //运行 点击 关闭
                    stopService(intent);
                    sv_processmanager_lockscreenclear.setToggle(false);
                } else {
                    startService(intent);
                    sv_processmanager_lockscreenclear.setToggle(true);
                }
                break;
        }
    }

    private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        @Override
        public int getCount() {
            return isShowSystem ? systemProcessInfos.size() + userProcessInfos.size() : userProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
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
                view = View.inflate(ProcessManagerActivity.this, R.layout.processmanager_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_processmanageritem_icon = (ImageView) view.findViewById(R.id.iv_processmanageritem_icon);
                viewHolder.tv_processmanageritem_name = (TextView) view.findViewById(R.id.tv_processmanageritem_name);
                viewHolder.tv_processmanageritem_memory = (TextView) view.findViewById(R.id.tv_processmanageritem_memory);
                viewHolder.cb_processmanageritem_isselect = (CheckBox) view.findViewById(R.id.cb_processmanageritem_isselect);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            //获取数据
            ProcessInfo processInfo;
            if (position <= userProcessInfos.size() - 1) {
                processInfo = userProcessInfos.get(position);
            } else {
                processInfo = systemProcessInfos.get(position - userProcessInfos.size());
            }
            //显示数据
            viewHolder.iv_processmanageritem_icon.setImageDrawable(processInfo.icon);
            long memorySize = processInfo.memorySize;
            //b-->MB
            String memSize = Formatter.formatFileSize(ProcessManagerActivity.this, memorySize);
            viewHolder.tv_processmanageritem_memory.setText("内存占用:" + memSize);
            viewHolder.tv_processmanageritem_name.setText(processInfo.name);
            // 在listview中实时改变的操作,一般不会去复用缓存的,因为checkbox状态是要实时改变的,所以checkbox状态是不能跟着去复用缓存的
            // 解决:一般会把操作状态的标示存放bean类,根据bean类来进行改变checkbox的状态
            viewHolder.cb_processmanageritem_isselect.setChecked(processInfo.isChecked);
            //如果是当前进程，要隐藏checkBox，在adapter中有if必须有else，防止缓存复用
            if (processInfo.packageName.equals(getPackageName())) {
                viewHolder.cb_processmanageritem_isselect.setVisibility(View.GONE);
            } else {
                viewHolder.cb_processmanageritem_isselect.setVisibility(View.VISIBLE);
            }
            return view;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(ProcessManagerActivity.this);
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8, 8, 8, 8);
            } else {
                textView = (TextView) convertView;
            }
            //设置显示用户进程和系统进程的个数
            //获取数据
            ProcessInfo processInfo;
            if (position <= userProcessInfos.size() - 1) {
                processInfo = userProcessInfos.get(position);
            } else {
                processInfo = systemProcessInfos.get(position - userProcessInfos.size());
            }
            //根据进程是否是系统进程设置显示样式文本
            textView.setText(processInfo.isSystem ? "系统进程(" + systemProcessInfos.size() + "个)" : "用户进程(" + userProcessInfos.size() + "个)");
            return textView;
        }

        @Override
        public long getHeaderId(int position) {
            //获取数据
            ProcessInfo processInfo;
            if (position <= userProcessInfos.size() - 1) {
                processInfo = userProcessInfos.get(position);
            } else {
                processInfo = systemProcessInfos.get(position - userProcessInfos.size());
            }
            return processInfo.isSystem ? 0 : 1;
        }
    }

    private class ViewHolder {
        ImageView iv_processmanageritem_icon;
        TextView tv_processmanageritem_name, tv_processmanageritem_memory;
        CheckBox cb_processmanageritem_isselect;
    }
}
