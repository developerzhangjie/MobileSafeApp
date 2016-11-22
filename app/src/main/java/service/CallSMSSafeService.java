package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import db.BlackNumberConstant;
import db.dao.BlackNumberDao;

/**
 * Created by Sin on 2016/9/26.
 * Description:
 */

public class CallSMSSafeService extends Service {

    private SMSReceiver mReceiver;
    private BlackNumberDao mBlackNumberDao;
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mMyPhoneStateListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBlackNumberDao = new BlackNumberDao(this);
        //注册广播接收者,短信拦截
        mReceiver = new SMSReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
        registerReceiver(mReceiver, intentFilter);
        //电话拦截
        mMyPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消短信监听
        unregisterReceiver(mReceiver);
        //取消电话监听
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);

    }

    //短信广播接收者
    private class SMSReceiver extends BroadcastReceiver implements Thread.UncaughtExceptionHandler {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Thread.setDefaultUncaughtExceptionHandler(this);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    try {
                        Bundle bundle = intent.getExtras();
                        //如果不为空
                        if (bundle != null) {
                            //将pdus里面的内容转化成Object[]数组
                            Object pdusData[] = (Object[]) bundle.get("pdus");
                            String format = intent.getStringExtra("format");
                            //解析短信
                            SmsMessage[] msg = new SmsMessage[pdusData.length];
                            for (int i = 0; i < msg.length; i++) {
                                byte pdus[] = (byte[]) pdusData[i];
                                msg[i] = SmsMessage.createFromPdu(pdus, format);
                            }
                            StringBuffer content = new StringBuffer();//获取短信内容
                            StringBuffer phoneNumber = new StringBuffer();//获取地址
                            //分析短信具体参数
                            for (SmsMessage temp : msg) {
                                content.append(temp.getMessageBody());
                                phoneNumber.append(temp.getOriginatingAddress());
                            }
                            int mode = mBlackNumberDao.queryBlackNumber(phoneNumber.toString());
                            if (mode == BlackNumberConstant.BACKNUMBER_SMS || mode == BlackNumberConstant.BALCKNUMBER_ALL) {
                                abortBroadcast();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Log.i("AAA", "uncaughtException   " + throwable);
        }
    }


    //电话监听
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    int mode = mBlackNumberDao.queryBlackNumber(incomingNumber);
                    if (mode == BlackNumberConstant.BLACKNUMBER_CALL || mode == BlackNumberConstant.BALCKNUMBER_ALL) {
                        System.out.println("电话拦截");
                        endCall();
                        //删除通话记录
                        final ContentResolver resolver = getContentResolver();
                        final Uri uri = Uri.parse("content://call_log/calls");
                        resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                resolver.delete(uri, "number=?", new String[]{incomingNumber});
                                resolver.unregisterContentObserver(this);
                            }
                        });
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
            }
        }
    }


    public void endCall() {
        try {
            // 1.5版本
            // I...stub : aidl 远程服务
            // 反射使用ServiceManager操作
            // 1.获取字节码文件
            // Class.forName("android.os.ServiceManager");
            // 通过类加载器获取字节码
            Class<?> loadClass = CallSMSSafeService.class.getClassLoader()
                    .loadClass("android.os.ServiceManager");
            // 2.获取其中的方法
            // name : 方法名
            // parameterTypes : 方法参数的类型
            Method method = loadClass.getDeclaredMethod("getService",
                    String.class);
            // 3.执行这个方法
            // method : 对象,如果调用的方法不是静态方法,需要指定方法所在的类的对象,如果是静态直接null
            // args : 方法的参数
            IBinder invoke = (IBinder) method.invoke(null,
                    Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
            iTelephony.endCall();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 挂断电话
    }
}
