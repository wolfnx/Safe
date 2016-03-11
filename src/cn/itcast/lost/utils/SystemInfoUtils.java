package cn.itcast.lost.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by wolfnx on 2016/2/24.
 */
public class SystemInfoUtils {

    /**
     * 返回进程的总个数
     * 因为getSystemServic是在activity中才有所以要传一个上下文进来
     * @return
     */
    public static int getProcessCount(Context context){
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获得当前的运行进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //获取当前手机上面总共有多少个进程
        return runningAppProcesses.size();
    }

    /**
     * 获得可用的内存大小
     * @param context
     * @return
     */
    public static long getAvailMem(Context context){

        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取内存的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取到剩余内存
        return memoryInfo.availMem;
    }

    public static long getTotalMem(Context context){
        /**
         * 获取到总内存,此api高版本手机才有，因此为了兼容最好就不要用
         * long totalMem = memoryInfo.totalMem;
         * 因为内存的信息都在proc文件内保存，因此要在这个文件夹内读出内存大小
         */
        try {
            //proc/meminfo配置文件的路径
            FileInputStream is = new FileInputStream(new File("/proc/meminfo"));

            //一读读一行
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String readLine = reader.readLine();

            StringBuffer sb = new StringBuffer();

            for(char c: readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            long totalMem = Long.parseLong(sb.toString())*1024;

            return totalMem;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
