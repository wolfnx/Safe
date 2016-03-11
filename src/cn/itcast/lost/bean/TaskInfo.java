package cn.itcast.lost.bean;

import android.graphics.drawable.Drawable;

/**
 * 進程管理信息類
 * Created by wolfnx on 2016/2/24.
 */
public class TaskInfo {

    private Drawable icon;

    private String packageName;

    private String appName;

    private long memorySize;
    //判断是否是用户进程
    private boolean userApk;
    //判断Item的条目是不是被勾选上了
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApk() {
        return userApk;
    }

    public void setUserApk(boolean userApk) {
        this.userApk = userApk;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", memorySize=" + memorySize +
                ", userApk=" + userApk +
                ", checked=" + checked +
                '}';
    }
}
