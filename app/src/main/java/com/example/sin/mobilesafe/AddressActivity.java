package com.example.sin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import db.dao.AddressDao;

/**
 * Created by Sin on 2016/9/26.
 * Description:
 */

public class AddressActivity extends Activity {
    private Button bn_address_query;
    private EditText et_address_phone;
    private TextView tv_address_phoneaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
    }

    private void initView() {
        bn_address_query = (Button) findViewById(R.id.bn_address_query);
        et_address_phone = (EditText) findViewById(R.id.et_address_phone);
        tv_address_phoneaddress = (TextView) findViewById(R.id.tv_address_phoneaddress);

        /*//归属地实时显示
        tv_address_phoneaddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                String address = AddressDao.getAddress(AddressActivity.this, number);
                if (!TextUtils.isEmpty(address)) {
                    tv_address_phoneaddress.setText("归属地：" + address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        bn_address_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_address_phone.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(AddressActivity.this, "请输入要查询的号码", Toast.LENGTH_SHORT).show();
                    //执行抖动动画
                    Animation shake = AnimationUtils.loadAnimation(AddressActivity.this, R.anim.shake);
                    et_address_phone.startAnimation(shake);
                    //振动
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    //设置振动的时间
                    //milliseconds : 振动的时间
                    vibrator.vibrate(100);//国产定制手机,执行默认的振动的时间  比如小米,单位毫秒
                    return;
                } else {
                    String address = AddressDao.getAddress(AddressActivity.this, number);
                    if (!TextUtils.isEmpty(address)) {
                        tv_address_phoneaddress.setText("归属地:" + address);
                    } else {
                        Toast.makeText(AddressActivity.this, "请输入有效号码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}
