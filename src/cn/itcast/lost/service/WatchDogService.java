package cn.itcast.lost.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.List;

import cn.itcast.lost.activity.EnterPwdActivity;
import cn.itcast.lost.db.dao.AppLockDao;

public class WatchDogService extends Service {

    private ActivityManager activityManager;
    private AppLockDao dao;
    private WatchDogReceiver watchDogReceiver;
    private List<String> appLockInfos;

    //标记当前的看门狗是否要停下来
    private boolean flag = false;
    //临时停止保护的包名
    public String tempStopProtectPackageName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 1.获取当前的任务栈
         * 2.取任务栈最上面的任务
         */
        //获取进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        watchDogReceiver = new WatchDogReceiver();

        IntentFilter filter = new IntentFilter();

        //注册一个内容观察者，用来观察新的未加锁程序被放到加锁中
        getContentResolver().registerContentObserver(Uri.parse("content://cn.itcast.lost.lock"),true,new AppLockContentObserver(new Handler()));

        //停止保护
        /**
         * 注册一个广播，锁屏时狗休息，解锁时，狗看门
         */

        //停止保护的包名
        filter.addAction("cn.itcast.lost.stopprotect");
        //锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //取消锁屏
        filter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(watchDogReceiver, filter);

        dao = new AppLockDao(this);

        appLockInfos = dao.findAll();

        startWatchDog();

    }

    /**
     * 由于看门狗一直在后台，为了避免程序阻塞所以必须开子线程
     */
    private void startWatchDog() {

        new Thread() {
            @Override
            public void run() {
                flag = true;
                /**
                 * 获取当前正在运行的任务栈,获取100个
                 * 此处需要权限:
                 * <uses-permission android:name="android.permission.GET_TASKS" />
                 */
                //一直看门，while(true)
                while (flag) {
                    List<ActivityManager.RunningTaskInfo> runningTasks =
                            activityManager.getRunningTasks(10);

                    //获取到最上面的进程
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);

                    //获取最顶端应用程序的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();

                    SystemClock.sleep(30);

                    //查找一下是否在程序锁数据库里面
                    //这个可以优化，改成从内存中寻找
                    if (appLockInfos.contains(packageName)) {
                        //需要临时取消保护因为用户输入了正确的密码
                        if (packageName.equals(tempStopProtectPackageName)) {

                        } else {
                            //服务不能直接向activity跳,需要设置flags
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //停止保护的对象
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }

                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;

        //反注册掉广播
        unregisterReceiver(watchDogReceiver);
        //手动回收
        watchDogReceiver = null;
    }

    /**
     * 看门狗广播
     */
    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             *此处将if---else if的形式分成了3个if,是因为上面的看门狗
             * 在子线程中运行，这边的广播刚接收到意图，还没来的及执行
             * 下面else if的判断，看门狗提前执行了。
             */
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //锁屏，让狗休息
                flag = false;
                tempStopProtectPackageName = null;
            }

            if (intent.getAction().equals("cn.itcast.lost.stopprotect")) {
                //获取停止保护的对象
                tempStopProtectPackageName= intent.getStringExtra("packageName");
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //解锁，让狗看门
                if (flag == false) {
                    startWatchDog();
                }

            }
        }
    }

    /**
     * 内容观察者
     */
    private class AppLockContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //当数据发生变化时再拿一次数据
            appLockInfos = dao.findAll();
        }
    }
}
