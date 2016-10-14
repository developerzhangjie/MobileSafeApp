package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.sin.mobilesafe.R;

import db.dao.AddressDao;
import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Created by Sin on 2016/9/27.
 * Description:
 */

public class AddressService extends Service {

    private MyPhoneStateListener mMyPhoneStateListener;
    private View mView;
    private WindowManager mWindowManager;
    private TextView tv_custom_address;
    private MyOutGoingCallReciver mMyOutGoingCallReciver;
    private TelephonyManager mTelephonyManager;
    private WindowManager.LayoutParams mParams;
    private int mWidth;
    private int mHeight;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mMyPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //外拨电话显示归属地注册广播接收者
        mMyOutGoingCallReciver = new MyOutGoingCallReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(mMyOutGoingCallReciver, intentFilter);

        //获取屏幕的宽度和高度
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://挂断状态
                    hideToast();
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String address = AddressDao.getAddress(AddressService.this, incomingNumber);
                    if (!TextUtils.isEmpty(address)) {
                        //Toast.makeText(AddressService.this, address, Toast.LENGTH_SHORT).show();
                        showToast(address);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                    break;
            }
        }
    }

    private class MyOutGoingCallReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String address = AddressDao.getAddress(context, number);
            if (!TextUtils.isEmpty(address)) {
                showToast(address);
            }
        }
    }

    /**
     * 自定义Toast，复制的Toast的系统代码
     */
    private void showToast(String address) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mView = View.inflate(AddressService.this, R.layout.custom_toast, null);
        //获取保存颜色的resId
        int resId = SharedPreferencesUtils.getInt(AddressService.this, Constants.ADDRESSDIALOG_COLOR_RESID, 0);
        mView.setBackgroundResource(resId);
        tv_custom_address = (TextView) mView.findViewById(R.id.tv_custom_address);
        tv_custom_address.setText(address);
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = 100;
        mParams.y = 100;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        setTouchEvent();
        mWindowManager.addView(mView, mParams);
    }

    //触摸事件
    private void setTouchEvent() {
        mView.setOnTouchListener(new View.OnTouchListener() {

            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //1.获取起始位置手指的坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.获取新位置手指的坐标
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        //3.计算偏移量
                        int changedX = newX - startX;
                        int changedY = newY - startY;
                        //4.将控件移动相应的偏移量，并重新绘制控件
                        mParams.x += changedX;
                        mParams.y += changedY;
                        //判断空间是否移出屏幕
                        if (mParams.x < 0) {
                            mParams.x = 0;
                        }
                        if (mParams.y < 0) {
                            mParams.y = 0;
                        }
                        if (mParams.x > (mWidth - v.getWidth())) {
                            mParams.x = mWidth - v.getWidth();
                        }
                        if (mParams.y > (mHeight - v.getHeight())) {
                            mParams.y = mHeight - v.getHeight();
                        }
                        //更新控件
                        mWindowManager.updateViewLayout(v, mParams);
                        //5.记录新的手指位置坐标
                        startX = newX;
                        startY = newY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 隐藏Toast的方法
     */
    private void hideToast() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWindowManager.removeView(mView);
            }
            mView = null;
            mWindowManager = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销监听电话的操作
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        //外拨电话取消监听
        unregisterReceiver(mMyOutGoingCallReciver);
    }
}
