package cn.itcast.lost.activity;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

/**
 * 清理缓存
 */
public class ClearCacheActivity extends Activity {

    @ViewInject(R.id.list_view)
    private ListView listView;

    private ClearCacheAdapter cacheAdapter;
    private PackageManager packageManager;
    private List<CacheInfo> cacheInfos;
    private List<CacheInfo> clearCacheInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
    }

    /**
     * 一旦程序后台运行，就需要进行页面的重新刷新
     */
    @Override
    protected void onStart() {
        super.onStart();
        initUi();
        initData();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {

        ViewUtils.inject(this);

        packageManager = getPackageManager();

        //这个集合用来装扫描出有缓存的app
        cacheInfos = new ArrayList<>();

        //此集合用来装将要删除缓存的app
        clearCacheInfos = new ArrayList<>();

    }

    /**
     * 获取应用程序的缓存
     *
     * @param packageInfo
     */
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //反射
            // Class<?> aClass = getClassLoader().loadClass("PackageManager");
            //通过反射获取当前的方法,因为知道当前的对象名字叫PackageManger,所以就不用上面这个语句
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo",
                    String.class, IPackageStatsObserver.class);
            //第一个参数表示这个方法由哪个对象调用
            method.invoke(packageManager, packageInfo.applicationInfo.packageName,
                    new MyIPackageStatsObserver(packageInfo));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo info;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            info = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            long externalCacheSize = pStats.externalCacheSize;
            //如果当有缓存的时候才打印
            if ((cacheSize + externalCacheSize) > 30000) {
                CacheInfo cacheInfo = new CacheInfo();
                String appName = info.applicationInfo.loadLabel(packageManager).toString();
                Drawable icon = info.applicationInfo.loadIcon(packageManager);

                cacheInfo.appName = appName;
                cacheInfo.icon = icon;
                cacheInfo.cacheSize = cacheSize + externalCacheSize;
                cacheInfos.add(cacheInfo);

            }
        }
    }

    static class CacheInfo {
        Drawable icon;
        long cacheSize;
        String appName;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {


                /**
                 * 接收2个参数
                 * 第一个接收一个包名
                 * 第二个接收一个aidl的对象
                 * *@hide  方法被阉割掉了，我们非要用的话就可以用反射
                 * public abstract void getPackageSizeInfo(String packageName, int userHandle,
                 *             IPackageStatsObserver observer);
                 * 此处也需要权限
                 * <uses-permission android:name="android.permission.GET_PACKAGE_SIZE" />
                 */

                //获取所有安装的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : installedPackages) {
                    getCacheSize(packageInfo);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**如果此处不给延时,就会导致下面代码运行过快，
                         *从而adapter中获取的list的大小总是不一样，有的时候不需要是
                         * 因为list形成的比较快，对adapter不影响，而此处形成就较慢
                         * 此处最好使用new handeler;
                         */
                        SystemClock.sleep(3000);
                        cacheAdapter = new ClearCacheAdapter();
                        listView.setAdapter(cacheAdapter);

                    }
                });
            }
        }.start();
    }

    private class ClearCacheAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            viewHolder holder;
            CacheInfo cacheInfo;

            if (convertView != null) {
                holder = (viewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(ClearCacheActivity.this, R.layout.item_clean_cache, null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_taskName = (TextView) convertView.findViewById(R.id.tv_cache_Name);
                holder.tv_cache_size = (TextView) convertView.findViewById(R.id.tv_cache_size);
                convertView.setTag(holder);
            }
            cacheInfo = cacheInfos.get(position);
            holder.iv_icon.setImageDrawable(cacheInfo.icon);
            holder.tv_taskName.setText(cacheInfo.appName);
            holder.tv_cache_size.setText("内存大小:" + Formatter.formatFileSize(ClearCacheActivity.this, cacheInfo.cacheSize));

            return convertView;
        }
    }

    static class viewHolder {

        private TextView tv_taskName;
        private TextView tv_cache_size;
        private ImageView iv_icon;
    }

    /**
     * 全部清除
     *
     * @param view
     */
    public void cleanAll(View view) {


        if (cacheInfos.size() == 0) {
            ToastUtils.showToast(ClearCacheActivity.this, "没有垃圾");
            return;
        }

        /**
         * 获取所有的方法
         * 此处需要权限
         * <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
         */
        Method[] methods = PackageManager.class.getMethods();


        for (Method method : methods) {
            if (method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager,Long.valueOf(getAvailableInternalMemorySize()-1), new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /**
         * 因为在迭代的时候不能直接remove里面的元素，
         * 直接删除会报错，所以放到下面的迭代里面删除
         */
        for (CacheInfo cacheInfo : cacheInfos) {
            clearCacheInfos.add(cacheInfo);
        }

        for (CacheInfo cacheInfo : clearCacheInfos) {
            cacheInfos.remove(cacheInfo);
        }

        cacheAdapter.notifyDataSetChanged();

        ToastUtils.showToast(ClearCacheActivity.this, "全部清除");

    }

    /**
     * 获取手机内部可用空间大小
     * @return
     */

    static public long getAvailableInternalMemorySize() {

        File path = Environment.getDataDirectory();

        //获取手机存储空间的大小
        while(true){
            StatFs stat = new StatFs(path.getPath());

            long blockSize = stat.getBlockSize();

            long availableBlocks = stat.getAvailableBlocks();


            return availableBlocks * blockSize*12;


        }
    }

}
