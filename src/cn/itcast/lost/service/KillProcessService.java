package cn.itcast.lost.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by wolfnx on 2016/2/27.
 */
public class KillProcessService extends Service {

    LockScreenReceiver lockScreenReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //创建一个锁屏广播
    private class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获得进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo c:runningAppProcesses){
                //将所有进程杀死
                activityManager.killBackgroundProcesses(c.processName);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //锁屏广播
        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个广播
        registerReceiver(lockScreenReceiver,intentFilter);

        /**如果想定时的去杀掉进程就可以采用以下代码
         * 获得一个定时器
        Timer timer=new Timer();

        //执行的任务
        TimerTask task=new TimerTask(){
            @Override
            public void run() {
                //写业务逻辑
            }
        };

        //进行定时调度
        timer.schedule(task,1000,1000);
         */

    }

    /**
     * 当应用程序退出时候要把广播反注册掉
     * 否则会报错，虽然不影响程序执行
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //当应用程序退出时候要把广播反注册掉
        unregisterReceiver(lockScreenReceiver);
        //手动回收
        lockScreenReceiver=null;
    }
}
