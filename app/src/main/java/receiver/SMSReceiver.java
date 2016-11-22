package receiver;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.sin.mobilesafe.R;

import service.GpsService;

/**Description:解析来自手机防盗的短信
 * Created by Sin on 2016/9/14.
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private AudioManager mAudioManager;
    private DevicePolicyManager mDevicePolicyManager;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 设备安全管理服务    2.2之前的版本是没有对外暴露的 只能通过反射技术获取
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 申请权限
        ComponentName componentName = new ComponentName(context, Admin.class);
        // 判断该组件是否有系统管理员的权限
        boolean isAdminActive = mDevicePolicyManager.isAdminActive(componentName);
        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)) {
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
                //需要加一个是否是安全号码判断,才能执行下面的代码
                if ("#*location*#".equals(content.toString())) {
                    //gps追踪
                    Intent service_intent = new Intent(context, GpsService.class);
                    context.startService(service_intent);//开启服务
                    abortBroadcast();//除非亲儿子，几乎不管用
                } else if ("#*alarm*#".equals(content.toString())) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                    mediaPlayer.setLooping(true);
                    //这两行是调音量的，管用
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            // TODO: 2016/9/22 后台能执行  锁屏能执行
                            while (true) {
                                try {
                                    int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 3;
                                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                    mediaPlayer.start();
                    abortBroadcast();
                } else if ("#*wipe*#".equals(content.toString())) {
                    if (isAdminActive) {
                        // 恢复出厂设置  (建议大家不要在真机上测试) 模拟器不支持该操作
                        //devicePolicyManager.wipeData(0);
                    }
                    abortBroadcast();
                } else if ("#*lockScreen*#".equals(content.toString())) {
                    if (isAdminActive) {
                        mDevicePolicyManager.lockNow(); // 锁屏
                        mDevicePolicyManager.resetPassword("2345", 0); // 设置锁屏密码
                    }
                    abortBroadcast();
                }
            }
        }
    }
}