package cn.itcast.lost.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import cn.itcast.lost.db.dao.BlackNumberDao;

public class CallSafeService extends Service {

    private BlackNumberDao blackNumberDao;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        blackNumberDao = new BlackNumberDao(this);//初始化黑名单
        //初始化短信广播
        InnerReceiver innerReceiver=new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");//过滤器(过滤短信）
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);//注册Receiver

    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            System.out.println("短信来了！");
            for (Object object : objects) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = smsMessage.getOriginatingAddress();//获取短信号码
                //通过电话号码查询拦截模式
                String mode = blackNumberDao.findNumber(originatingAddress);
                System.out.println(mode);
                if(mode.equals("1")){
                    abortBroadcast();
                }else if(mode.equals("3")){
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
