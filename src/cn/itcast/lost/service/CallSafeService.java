package cn.itcast.lost.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

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

        //获取系统的电话服务(转换一下）
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

        //初始化短信广播
        InnerReceiver innerReceiver=new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");//过滤器(过滤短信）
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);//注册Receiver

    }

    private class MyPhoneStateListener extends PhoneStateListener{
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            /*@see TelephonyManager#CALL_STATE_IDLE  电话闲置状态
             * @see TelephonyManager#CALL_STATE_RINGING 电话响铃状态
             * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通状态*/
            switch (state){
                //响铃状态
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = blackNumberDao.findNumber(incomingNumber);

                    System.out.println(incomingNumber);

                    if(mode.equals("1")||mode.equals("2")){
                        System.out.println("拦截电话");
                        Uri uri = Uri.parse("content://call_log/calls");
                        //内容观察者，true表示前缀满足
                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));
                        //挂掉电话
                        endCall();
                    }
                    break;
            }
        }
    }

    private class  MyContentObserver extends ContentObserver{

        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber=incomingNumber;
        }

        //当数据改变时调用的方法
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);

            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }
    }

    /**
     * 删除通话记录
     * 需要权限
     * <uses-permission android:name="android.permission.READ_CALL_LOG" />
     * <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }

    /**
     * 挂断电话
     * 需要权限
     * <uses-permission android:name="android.permission.CALL_PHONE" />
     */
    private void endCall(){
        try {
            //通过类加载器加载ServiceManager
            Class<?> aClass = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射来获取这个方法
            Method method = aClass.getDeclaredMethod("getService", String.class);

            IBinder iBinder=(IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
