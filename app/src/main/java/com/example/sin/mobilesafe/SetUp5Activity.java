package com.example.sin.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Description:开启防盗保护
 * Created by Sin on 2016/9/8.
 */
public class SetUp5Activity extends SetUpBaseActivity {
    private CheckBox cb_setup5_protected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);
        initView();
    }

    private void initView() {
        cb_setup5_protected = (CheckBox) findViewById(R.id.cb_setup5_protected);
        //回显操作（checkBox的状态）
        boolean sp_protected = SharedPreferencesUtils.getBoolean(this, Constants.PROTECTED, false);
        //设置checkBox的状态
        cb_setup5_protected.setChecked(sp_protected);
        cb_setup5_protected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(SetUp5Activity.this, Constants.PROTECTED, isChecked);
            }
        });
    }

    @Override
    protected boolean previous_activity() {
        Intent intent = new Intent(this, SetUp4Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean next_activity() {
        if (!cb_setup5_protected.isChecked()) {
            Toast.makeText(SetUp5Activity.this, "请开启手机防盗保护", Toast.LENGTH_SHORT).show();
            return true;
        }
        SharedPreferencesUtils.saveBoolean(this, Constants.ISFIRSTENTER, true);
        //2.跳转到手机防盗界面
        Intent intent = new Intent(this, LostFindActivity.class);
        startActivity(intent);
        return false;
    }
}
