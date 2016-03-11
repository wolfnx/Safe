package cn.itcast.lost.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.activity.AppManagerActivity;
import cn.itcast.lost.activity.TaskManagerActivity;
import cn.itcast.lost.bean.TaskInfo;
import cn.itcast.safe.R;

/**
 * 获取所有进程的工具类
 * Created by wolfnx on 2016/2/24.
 */
public class TaskInfoParser {

    public static List<TaskInfo> getTaskInfos(Context mContext){

        List<TaskInfo> taskInfos = new ArrayList<>();
        //获取包管理(因为获取图片要从包里获取)
        PackageManager packageManager = mContext.getPackageManager();

        //获取到进程管理者
        ActivityManager activityManager =(ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);

        //获取当前运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        for( ActivityManager.RunningAppProcessInfo processInfo:runningAppProcesses){

            TaskInfo taskInfo = new TaskInfo();

            //获取进程的名字，实际上是包名
            String processName = processInfo.processName;
            taskInfo.setPackageName(processName);

            try {
                //获取包的基本信息
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //获取应用图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                //获取应用的名字
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);
                //下面括号内表示的PID(打开任务管理器可以看到
                Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{processInfo.pid});
                long totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty()*1024;
                taskInfo.setMemorySize(totalPrivateDirty);

                int flags = packageInfo.applicationInfo.flags;
                if((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                    //系统应用
                    taskInfo.setUserApk(false);
                }else {
                    //用户应用
                    taskInfo.setUserApk(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //因为有的系统应用没有图标，如果不给默认图标就会报错
                taskInfo.setIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName("系统程序");
                taskInfo.setMemorySize(1024);
            }
            //此处为什么要将这个放到下面，是因为有的系统程序我们是拿不到的所以
           // 我们可以将我们自定义的图片和名称对象放进去
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
