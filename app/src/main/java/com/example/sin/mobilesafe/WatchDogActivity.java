package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sin on 2016/10/11.
 * Description:
 */

public class WatchDogActivity extends Activity {

    private Intent mIntent;
    private String mPackageName;
    private ApplicationInfo mApplicationInfo;
    private CharSequence name;
    private Drawable icon;
    private ImageView iv_watchdog_icon;
    private TextView tv_watchdog_name;
    private EditText et_watchdog_psw;
    private Button bn_watchdog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchdog);

        iv_watchdog_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
        tv_watchdog_name = (TextView) findViewById(R.id.tv_watchdog_name);
        et_watchdog_psw = (EditText) findViewById(R.id.et_watchdog_psw);
        bn_watchdog = (Button) findViewById(R.id.bn_watchdog);
        bn_watchdog.setOnClickListener(new View.OnClickListener() {
            private String psw;

            @Override
            public void onClick(View v) {
                //获取输入的内容
                psw = et_watchdog_psw.getText().toString().trim();
                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(WatchDogActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if ("123".equals(psw)) {
                        //告诉服务当前应用程序已经解锁,不需要在弹出加锁界面
                        //广播,自定义发送一个广播,服务接受广播来进行操作
                        Intent intent = new Intent();
                        intent.setAction("finishActivity");
                        intent.putExtra("packageName", mPackageName);
                        sendBroadcast(intent);
                        finish();
                    } else {
                        Toast.makeText(WatchDogActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        mIntent = getIntent();
        mPackageName = mIntent.getStringExtra("packageName");
        PackageManager pm = getPackageManager();
        try {
            mApplicationInfo = pm.getApplicationInfo(mPackageName, 0);
            name = mApplicationInfo.loadLabel(pm).toString();
            tv_watchdog_name.setText(name);
            icon = mApplicationInfo.loadIcon(pm);
            iv_watchdog_icon.setImageDrawable(icon);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
