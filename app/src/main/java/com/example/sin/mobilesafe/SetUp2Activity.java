package com.example.sin.mobilesafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Description:手机卡绑定
 * Created by Sin on 2016/9/8.
 */
public class SetUp2Activity extends SetUpBaseActivity {
    RelativeLayout rel_setup2_sim;
    ImageView iv_setup2_isLock;
    private TelephonyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initView();
    }

    private void initView() {
        rel_setup2_sim = (RelativeLayout) findViewById(R.id.rel_setup2_sim);
        iv_setup2_isLock = (ImageView) findViewById(R.id.iv_setup2_islock);
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //回显操作
        String sp_sim = SharedPreferencesUtils.getString(SetUp2Activity.this, Constants.SIM, "");
        if (TextUtils.isEmpty(sp_sim)) {
            //如果是空，就显示unlock
            iv_setup2_isLock.setImageResource(R.drawable.unlock);
        } else {
            //如果不是空，就显示lock
            iv_setup2_isLock.setImageResource(R.drawable.lock);
        }
        rel_setup2_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取保存的sim卡，有的话显示加锁的图片，没有的话，显示开锁的图片
                String sp_sim = SharedPreferencesUtils.getString(SetUp2Activity.this, Constants.SIM, "");
                //2.判断是否为空
                if (TextUtils.isEmpty(sp_sim)) {
                    //如果为空，说明没有保存sim卡，点击一下之后，需要变为lock图片
                    //绑定sim卡
                    //1.获取sim卡
                    @SuppressLint("HardwareIds") String sim = manager.getSimSerialNumber();//获取sim卡序列号
                    //2.保存sim卡
                    SharedPreferencesUtils.saveString(SetUp2Activity.this, Constants.SIM, sim);
                    //3.点击，切换图片
                    iv_setup2_isLock.setImageResource(R.drawable.lock);
                } else {
                    //解绑sim卡
                    SharedPreferencesUtils.saveString(SetUp2Activity.this, Constants.SIM, "");
                    //如果不为空，说明已经保存了密码，点击一下，需要切换为解绑状态，就是unlock图片
                    iv_setup2_isLock.setImageResource(R.drawable.unlock);
                }
            }
        });
    }

    @Override
    protected boolean previous_activity() {
        Intent intent = new Intent(this, SetUp1Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean next_activity() {
        Intent intent = new Intent(this, SetUp3Activity.class);
        startActivity(intent);
        return false;
    }
}
