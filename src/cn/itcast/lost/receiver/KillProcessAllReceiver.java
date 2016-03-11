package cn.itcast.lost.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.itcast.lost.utils.ToastUtils;

/**
 * Created by wolfnx on 2016/2/28.
 */
public class KillProcessAllReceiver extends BroadcastReceiver {
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
        ToastUtils.showToast(context,"清理完成");
    }
}
