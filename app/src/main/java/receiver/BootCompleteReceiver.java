package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import service.ProtectedService;
import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Created by Sin on 2016/9/12.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //1.获取保存的sim卡
        String sp_sim = SharedPreferencesUtils.getString(context, Constants.SIM, "");
        //2.获取本机的sim卡
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sim = telephonyManager.getSimSerialNumber();
        //3.比较两个sim卡是否为空
        if (!TextUtils.isEmpty(sp_sim) && !TextUtils.isEmpty(sim)) {
            if (!sp_sim.equals(sim)) {
                //不相等，就发送短信
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("15376771229", null, "Help me!", null, null);
            }
        }
        //开启前台进程
        context.startService(new Intent(context, ProtectedService.class));
    }
}
