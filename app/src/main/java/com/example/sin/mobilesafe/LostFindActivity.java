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

/**description：防盗中心
 * Created by Sin on 2016/9/8.
 */
public class LostFindActivity extends Activity implements View.OnClickListener {

    private TextView tv_lostFind_setup;
    private TextView tv_lostFind_safeNumber;
    private ImageView iv_lostFind_protected;
    private RelativeLayout rel_lostFind_protected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        initView();
        //重新设置向导
        tv_lostFind_setup.setOnClickListener(this);
        //开启或关闭手机防盗保护
        rel_lostFind_protected.setOnClickListener(this);
    }

    private void initView() {
        tv_lostFind_setup = (TextView) findViewById(R.id.tv_lostFind_setup);
        tv_lostFind_safeNumber = (TextView) findViewById(R.id.tv_lostFind_safeNumber);
        iv_lostFind_protected = (ImageView) findViewById(R.id.iv_lostFind_protected);
        rel_lostFind_protected = (RelativeLayout) findViewById(R.id.rel_lostFind_protected);
        //根据保存的安全号码和是否开启手机防盗保护的状态进行显示操作
        //安全号码的显示状态
        tv_lostFind_safeNumber.setText(SharedPreferencesUtils.getString(this, Constants.SAFENUMBER, ""));
        //手机防盗的显示状态
        boolean sp_protected = SharedPreferencesUtils.getBoolean(this, Constants.PROTECTED, false);
        if (sp_protected) {
            iv_lostFind_protected.setImageResource(R.drawable.lock);
        } else {
            iv_lostFind_protected.setImageResource(R.drawable.unlock);
        }
    }

    //所有的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //开启或关闭手机防盗保护
            case R.id.rel_lostFind_protected:
                //获取当前手机防盗保护的保存的状态
                boolean sp_protected = SharedPreferencesUtils.getBoolean(LostFindActivity.this, Constants.PROTECTED, false);
                if (sp_protected) {
                    //如果已经开启，那么点击之后，需要关闭
                    //重新保存状态
                    SharedPreferencesUtils.saveBoolean(LostFindActivity.this, Constants.PROTECTED, false);
                    //更改图标
                    iv_lostFind_protected.setImageResource(R.drawable.unlock);
                } else {
                    //如果没有开启，点击之后，需要开启
                    //重新保存状态
                    SharedPreferencesUtils.saveBoolean(LostFindActivity.this, Constants.PROTECTED, true);
                    //更改图标
                    iv_lostFind_protected.setImageResource(R.drawable.lock);
                }
                break;
            //重新设置向导
            case R.id.tv_lostFind_setup:
                Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
