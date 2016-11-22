package com.example.sin.mobilesafe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import receiver.Admin;

/**
 * Description:激活设备管理员
 * Created by Sin on 2016/9/8.
 */
public class SetUp4Activity extends SetUpBaseActivity {
    protected static final int REQUEST_CODE_ENABLE_ADMIN = 20;
    private RelativeLayout rel_setup4_policy;
    private ImageView iv_setup4_activation;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initView();
    }

    private void initView() {
        rel_setup4_policy = (RelativeLayout) findViewById(R.id.rel_setup4_policy);
        iv_setup4_activation = (ImageView) findViewById(R.id.iv_setup4_activation);
        //找到设备管理器
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, Admin.class);
        //回显操作
        if (devicePolicyManager.isAdminActive(componentName)) {
            iv_setup4_activation.setImageResource(R.drawable.admin_activated);
        } else {
            iv_setup4_activation.setImageResource(R.drawable.admin_inactivated);
        }
        //点击事件
        rel_setup4_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //激活 点击-->取消激活 未激活 点击———>激活
                //判断是否激活
                if (devicePolicyManager.isAdminActive(componentName)) {
                    //现处于已激活状态 点击之后变成未激活
                    devicePolicyManager.resetPassword("", 0);
                    devicePolicyManager.removeActiveAdmin(componentName);//设置取消激活超级管理员
                    //修改图片
                    iv_setup4_activation.setImageResource(R.drawable.admin_inactivated);
                } else {
                    //现处于未激活状态 点击之后变成激活
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    //标示要激活那个超级管理员
                    //mDeviceAdminSample : 超级管理员的标示
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    //设置描述信息
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "黑马手机卫士");
                    //跳转操作
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                }
            }
        });
    }

    //这个方法进行更改图标的操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断请求码是否一致
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (devicePolicyManager.isAdminActive(componentName)) {
                //已经激活
                iv_setup4_activation.setImageResource(R.drawable.admin_activated);
            } else {
                iv_setup4_activation.setImageResource(R.drawable.admin_inactivated);
            }
        }
    }

    @Override
    protected boolean previous_activity() {
        Intent intent = new Intent(this, SetUp3Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean next_activity() {
        //判断是否激活了超级管理员‘
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Toast.makeText(this, "请激活超级管理员", Toast.LENGTH_SHORT).show();
            return true;
        }
        Intent intent = new Intent(this, SetUp5Activity.class);
        startActivity(intent);
        return false;
    }
}
