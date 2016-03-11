package cn.itcast.lost.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;

/**
 * 获取所有应用程序的工具类
 * Created by wolfnx on 2016/1/31.
 */
public class AppInfoParser {
    /**
     * 获取packageManager需要在activity里，所以需要传上下文过来
     * 获取手机上APP的信息
     *
     * @param mContext
     * @return
     */
    public static List<AppInfo> getAppInfos(Context mContext) {

        List<AppInfo> list = new ArrayList<AppInfo>();
        //获取包的管理者
        PackageManager packageManager = mContext.getPackageManager();
        //获取整个手机上的安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            //获取应用程序的图标
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);
            //获取应用程序的报名
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //获取包名
            String packageName = packageInfo.applicationInfo.packageName;
            appInfo.setAppPackageName(packageName);
            //获取应用程序大小
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //apk长度
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);

            //获取到安装应用程序的标记
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统APP
                appInfo.setUserApp(false);
            } else {
                //表示用户APP
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示在SD卡
                appInfo.setIsRom(false);
            } else {
                //表示在系统内存
                appInfo.setIsRom(true);
            }

            list.add(appInfo);

        }
        return list;
    }
}
