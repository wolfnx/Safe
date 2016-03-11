package cn.itcast.lost.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.itcast.lost.service.KillProcessWidgetService;

/**
 * Created by wolfnx on 2016/2/28.
 */
public class MyAppWidget extends AppWidgetProvider {

    /**
     * 第一次创建的时候才会调用当前的生命周期的方法
     *
     * 当前的广播的生命周期只有10秒钟。
     * 不能做耗时的操作
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    /**
     * 当桌面上面所有的桌面小控件都删除的时候才调用当前这个方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
