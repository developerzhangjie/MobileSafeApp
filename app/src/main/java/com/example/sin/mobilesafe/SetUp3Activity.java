package com.example.sin.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Description:设置安全号码
 * Created by Sin on 2016/9/8.
 */
public class SetUp3Activity extends SetUpBaseActivity {
    private EditText et_setup3_safenumber;
    private Button bn_setup3_selectContacts;
    private final int SELECTCONTACTS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initView();
        //为按钮添加选择联系人的点击事件
        bn_setup3_selectContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetUp3Activity.this, ContactsActivity.class);
                startActivityForResult(intent, SELECTCONTACTS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String number = data.getStringExtra("number");
            et_setup3_safenumber.setText(number);
        }
    }

    private void initView() {
        et_setup3_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
        bn_setup3_selectContacts = (Button) findViewById(R.id.bn_setup3_selectContacts);
        //回显操作
        String sp_safeNumber = SharedPreferencesUtils.getString(this, Constants.SAFENUMBER, "");
        et_setup3_safenumber.setText(sp_safeNumber);
        //设置文本框的光标在文本最后一位的后面
        if (!TextUtils.isEmpty(sp_safeNumber)) {
            et_setup3_safenumber.setSelection(sp_safeNumber.length());
        }
    }

    @Override
    protected boolean previous_activity() {
        Intent intent = new Intent(this, SetUp2Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean next_activity() {
        //获取本机号码
        String safeNumber = et_setup3_safenumber.getText().toString().trim();
        if (TextUtils.isEmpty(safeNumber)) {
            //如果为空，就提示输入安全号码
            Toast.makeText(this, "请输入安全号码", Toast.LENGTH_SHORT).show();
            return true;
        }
        //不为空，就保存号码
        SharedPreferencesUtils.saveString(this, Constants.SAFENUMBER, safeNumber);
        Intent intent = new Intent(this, SetUp4Activity.class);
        startActivity(intent);
        return false;
    }


}
