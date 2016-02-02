package cn.itcast.lost.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by wolfnx on 2016/1/31.
 */
public class AppInfo {

    //程序的图片
    private Drawable icon;
    //程序的名字
    private String apkName;
    /**
     * 是用户APP还是系统APP
     * true表示用户APP,false则相反
     */
    private boolean userApp;
    /**
     * 放置位置
     */
    private boolean isRom;
    //包名
    private String appPackageName;
    //所占内存的大小
    private Long apkSize;

    public Long getApkSize() {
        return apkSize;
    }

    public void setApkSize(Long apkSize) {
        this.apkSize = apkSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "apkName='" + apkName + '\'' +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", appPackageName='" + appPackageName + '\'' +
                ", apkSize='" + apkSize + '\'' +
                '}';
    }
}
