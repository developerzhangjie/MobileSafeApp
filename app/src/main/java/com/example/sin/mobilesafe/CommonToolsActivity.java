package com.example.sin.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import engine.SMSEngine;
import service.WatchDogService1;
import ui.SettingView;
import utils.ServicUtil;

/**
 * Created by Sin on 2016/9/21.
 * Description:常用工具模块
 */

public class CommonToolsActivity extends Activity implements View.OnClickListener {
    private SettingView sv_commontools_address;
    private SettingView sv_setting_common_phoneNum;
    private SettingView sv_commontools_watchdog;
    private SettingView sv_commontools_watchdog_service1;
    private SettingView sv_commontools_watchdog_service2;
    private SettingView sv_commontools_backup;
    private SettingView sv_commontools_recovery;
    private String json;
    private String[] elements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commontools);
        initView();
    }

    private void initView() {
        sv_commontools_address = (SettingView) findViewById(R.id.sv_commontools_address);
        sv_setting_common_phoneNum = (SettingView) findViewById(R.id.sv_setting_common_phoneNum);
        sv_commontools_watchdog = (SettingView) findViewById(R.id.sv_commontools_watchdog);
        sv_commontools_watchdog_service1 = (SettingView) findViewById(R.id.sv_commontools_watchdog_service1);
        sv_commontools_watchdog_service2 = (SettingView) findViewById(R.id.sv_commontools_watchdog_service2);
        sv_commontools_backup = (SettingView) findViewById(R.id.sv_commontools_backup);
        sv_commontools_recovery = (SettingView) findViewById(R.id.sv_commontools_recovery);
        //设置监听事件
        sv_commontools_address.setOnClickListener(this);
        sv_setting_common_phoneNum.setOnClickListener(this);
        sv_commontools_watchdog.setOnClickListener(this);
        sv_commontools_watchdog_service1.setOnClickListener(this);
        sv_commontools_watchdog_service2.setOnClickListener(this);
        sv_commontools_backup.setOnClickListener(this);
        sv_commontools_recovery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //号码归属地查询
            case R.id.sv_commontools_address:
                Intent intent1 = new Intent(CommonToolsActivity.this, AddressActivity.class);
                startActivity(intent1);
                break;
            //常用号码查询
            case R.id.sv_setting_common_phoneNum:
                Intent intent2 = new Intent(CommonToolsActivity.this, CommonNumberActivity.class);
                startActivity(intent2);
                break;
            //程序锁管理
            case R.id.sv_commontools_watchdog:
                Intent intent3 = new Intent(CommonToolsActivity.this, AppLockActivity.class);
                startActivity(intent3);
                break;
            //短信备份
            case R.id.sv_commontools_backup:
                SMSEngine.verifyStoragePermissions(CommonToolsActivity.this);
                final ProgressDialog progressDialog = new ProgressDialog(CommonToolsActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SMSEngine.verifyStoragePermissions(CommonToolsActivity.this);
                        SMSEngine.getSMS(CommonToolsActivity.this, new SMSEngine.ShowProgress() {
                            @Override
                            public void setMax(int max) {
                                progressDialog.setMax(max);
                            }

                            @Override
                            public void setProgress(int progress) {
                                progressDialog.setProgress(progress);
                            }
                        });
                        progressDialog.dismiss();
                    }
                }.start();
                break;
            //短信还原
            case R.id.sv_commontools_recovery:
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/storage/emulated/0/Android/data/com.example.sin.mobilesafe/cache/shit.txt")));
                    while (true) {
                        String str = bufferedReader.readLine();
                        if (str == null) {
                            break;
                        }
                        json += str + "\n";
                    }
                    ContentResolver resolver = getContentResolver();
                    elements = json.split("\t");
                    Uri uri = Uri.parse("content://sms/");
                    for (int i = 0; i < elements.length; i += 4) {
                        ContentValues values = new ContentValues();
                        values.put("address", elements[i]);
                        values.put("date", elements[i + 1]);
                        values.put("type", elements[i + 2]);
                        values.put("body", elements[i + 3]);
                        resolver.insert(uri, values);
                    }
                    Toast.makeText(CommonToolsActivity.this, "短信成功恢复", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //开启电子狗服务1
            case R.id.sv_commontools_watchdog_service1:
                Intent intent4 = new Intent(CommonToolsActivity.this, WatchDogService1.class);
                if (ServicUtil.isServiceRunning(getApplicationContext(), "service.WatchDogService1")) {
                    //服务正在运行，点击关闭
                    stopService(intent4);
                    sv_commontools_watchdog_service1.setToggle(false);
                } else {
                    startService(intent4);
                    sv_commontools_watchdog_service1.setToggle(true);
                }
                break;
            //开启电子狗服务2
            case R.id.sv_commontools_watchdog_service2:
                Intent intent5 = new Intent();
                intent5.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                startActivity(intent5);
                break;
        }
    }

    //回显操作
    @Override
    protected void onStart() {
        super.onStart();
        //电子狗服务1
        if (ServicUtil.isServiceRunning(getApplicationContext(), "service.WatchDogService1")) {
            sv_commontools_watchdog_service1.setToggle(true);
        } else {
            sv_commontools_watchdog_service1.setToggle(false);
        }
        //电子狗服务2
        if (ServicUtil.isServiceRunning(getApplicationContext(), "service.WatchDogService2")) {
            sv_commontools_watchdog_service2.setToggle(true);
        } else {
            sv_commontools_watchdog_service2.setToggle(false);
        }
    }
}
