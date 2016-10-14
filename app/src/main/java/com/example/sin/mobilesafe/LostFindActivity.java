package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Created by Sin on 2016/9/8.
 */
public class LostFindActivity extends Activity implements View.OnClickListener {

    private TextView tv_lostfind_setup;
    private TextView tv_lostfind_safeNumber;
    private ImageView iv_lostfind_protected;
    private RelativeLayout rel_lostfind_protected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        initView();
        //重新设置向导
        tv_lostfind_setup.setOnClickListener(this);
        //开启或关闭手机防盗保护
        rel_lostfind_protected.setOnClickListener(this);
    }

    private void initView() {
        tv_lostfind_setup = (TextView) findViewById(R.id.tv_lostfind_setup);
        tv_lostfind_safeNumber = (TextView) findViewById(R.id.tv_lostfind_safeNumber);
        iv_lostfind_protected = (ImageView) findViewById(R.id.iv_lostfind_protected);
        rel_lostfind_protected = (RelativeLayout) findViewById(R.id.rel_lostfind_protected);
        //根据保存的安全号码和是否开启手机防盗保护的状态进行显示操作
        //安全号码的显示状态
        tv_lostfind_safeNumber.setText(SharedPreferencesUtils.getString(this, Constants.SAFENUMBER, ""));
        //手机防盗的显示状态
        boolean sp_protected = SharedPreferencesUtils.getBoolean(this, Constants.PROTECTED, false);
        if (sp_protected) {
            iv_lostfind_protected.setImageResource(R.drawable.lock);
        } else {
            iv_lostfind_protected.setImageResource(R.drawable.unlock);
        }
    }

    //所有的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //开启或关闭手机防盗保护
            case R.id.rel_lostfind_protected:
                //获取当前手机防盗保护的保存的状态
                boolean sp_protected = SharedPreferencesUtils.getBoolean(LostFindActivity.this, Constants.PROTECTED, false);
                if (sp_protected) {
                    //如果已经开启，那么点击之后，需要关闭
                    //重新保存状态
                    SharedPreferencesUtils.saveBoolean(LostFindActivity.this, Constants.PROTECTED, false);
                    //更改图标
                    iv_lostfind_protected.setImageResource(R.drawable.unlock);
                } else {
                    //如果没有开启，点击之后，需要开启
                    //重新保存状态
                    SharedPreferencesUtils.saveBoolean(LostFindActivity.this, Constants.PROTECTED, true);
                    //更改图标
                    iv_lostfind_protected.setImageResource(R.drawable.lock);
                }
                break;
            //重新设置向导
            case R.id.tv_lostfind_setup:
                Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
