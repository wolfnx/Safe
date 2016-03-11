package cn.itcast.lost.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import cn.itcast.lost.receiver.MyAppWidget;
import cn.itcast.lost.utils.SystemInfoUtils;
import cn.itcast.safe.R;

public class KillProcessWidgetService extends Service {

    private AppWidgetManager widgetManager;
    private Timer timer;
    private TimerTask task;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //桌面小控件管理者
        widgetManager = AppWidgetManager.getInstance(this);

        //初始化定时器,每隔5s清理一下
        timer=new Timer();
        //初始化一个定时任务
        task=new TimerTask(){
            @Override
            public void run() {

                /*System.out.println("我只能生存5s");*/

                //第二个从参数表示用哪个广播去处理当前控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidget.class);

                //把当前的布局文件添加进来
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                /**
                 * 需要注意。这个里面findingviewyid这个方法
                 * 设置当前文本里面一共有多少个进程
                 */
                remoteViews.setTextViewText(R.id.process_count,"当前剩余的进程:"+ SystemInfoUtils.getProcessCount(getApplicationContext())+"个");
                remoteViews.setTextViewText(R.id.process_memory,"当前剩余内存"+ Formatter.formatFileSize(getApplicationContext(),SystemInfoUtils.getAvailMem(getApplicationContext())));

                Intent intent = new Intent();

                //系统不知道要使用哪个广播，所以要用隐示意图
                intent.setAction("cn.itcast.xx");//此处包名随便取


                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                //设置button的点击事件
                remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);


                //更新桌面
                widgetManager.updateAppWidget(componentName,remoteViews);
            }
        };
        //每隔5s执行一次任务
        timer.schedule(task,0,5000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //优化代码
        if(timer != null || task != null){
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        }
    }
}
