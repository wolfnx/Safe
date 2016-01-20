package cn.itcast.lost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
/**
 * 监听手机开机的广播
 * @author wolfnx
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("lost",
				Context.MODE_PRIVATE);
		boolean protect = sp.getBoolean("OpenProtect", false);
		// 只有在防盗保护开启的前提下才进行sim卡判断
		if (protect) {
			String sim = sp.getString("sim", null);// 获取绑定的sim卡

			if (!TextUtils.isEmpty(sim)) {
				// 获取当前手机的sim卡
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSim = tm.getSimSerialNumber() + "111";// 拿到当前手机的sim卡

				if (sim.equals(currentSim)) {
					System.out.println("手机安全");
				} else {
					System.out.println("手机不安全");
					String phone=sp.getString("SafePhone", "");//取出安全号码
					System.out.println(phone);
					SmsManager smsManager=SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "Sim Card Changed", null, null);
				}
			}
		}
	}

}
