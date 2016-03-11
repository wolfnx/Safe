package cn.itcast.lost.activity;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;


import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.engine.AppInfoParser;
import cn.itcast.safe.R;

/**
 * 软件管理
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;
    private LinearLayout ll_pt;
    private PopupWindow popupWindow = null;
    private AppInfo clickAppInfo;

    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sd)
    private TextView tvSD;
    @ViewInject(R.id.tv_app)
    private TextView tvApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUi();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pt.setVisibility(View.INVISIBLE);
            AppManagerAdapter adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);

        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //得到手机上的所有应用程序
                appInfos = AppInfoParser.getAppInfos(AppManagerActivity.this);
                //将所有app的集合分成===用户app集合+系统app
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);//添加到用户app集合
                    } else {
                        systemAppInfos.add(appInfo);//添加到系统app集合
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        ll_pt = (LinearLayout) findViewById(R.id.ll_pt);
        //在加载图像时将ProgressBar显示出来
        ll_pt.setVisibility(View.VISIBLE);
        ViewUtils.inject(this);//注入注解
        //获取Rom内存的剩余空间
        Long rom = Environment.getDataDirectory().getFreeSpace();
        //获取SD卡存储空间
        Long sdRom = Environment.getExternalStorageDirectory().getFreeSpace();

        tvRom.setText("内存可用:" + Formatter.formatFileSize(this, rom));
        tvSD.setText("SD卡可用:" + Formatter.formatFileSize(this, sdRom));

        /*//以下部分用于处理卸载响应
        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);*/


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param view
             * @param firstVisibleItem 第一个可见的条目位置
             * @param visibleItemCount 一页可以展示多少个
             * @param totalItemCount   总共的条目
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        //系统应用程序
                        tvApp.setText("系统程序(" + systemAppInfos.size() + ")");
                    } else {
                        //用户应用程序
                        tvApp.setText("用户程序(" + userAppInfos.size() + ")");
                    }
                }
            }
        });

        //点击后显示:启动、分享、卸载
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean showing = false;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前的items对象
                Object object = listView.getItemAtPosition(position);

                if (object != null && object instanceof AppInfo) {
                    //一点击就获取到了当前items的对象，强转成Appinfo对象
                    clickAppInfo = (AppInfo) object;
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.items_popup, null);

                    //获得启动、分享、卸载、详情
                    LinearLayout start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                    LinearLayout share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                    LinearLayout uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    LinearLayout detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);
                    //设置监听
                    start.setOnClickListener(AppManagerActivity.this);
                    share.setOnClickListener(AppManagerActivity.this);
                    uninstall.setOnClickListener(AppManagerActivity.this);
                    detail.setOnClickListener(AppManagerActivity.this);
                    if (showing) {
                        popupWindow.dismiss();
                    }

                    //-2表示包裹内容
                    popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, -2);
                    //给popupwindow设置背景不然的话下面的动画效果出不来，设置成透明，将蓝色遮盖掉
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //增加缩放动画
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, ScaleAnimation.RELATIVE_TO_SELF);
                    //设置缩放时间
                    scaleAnimation.setDuration(300);
                    //将动画设置在contentview上
                    contentView.startAnimation(scaleAnimation);


                    int[] location = new int[2];
                    //获取当前item的位置
                    view.getLocationInWindow(location);
                    //因为x轴是固定的所以给了70，y轴需要实时获取
                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 180, location[1]+12);

                    showing = popupWindow.isShowing();
                    System.out.println(showing);

                }
            }
        });
    }

/*    //卸载广播
    class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("卸载广播");
        }
    }*/

    //启动、卸载、分享、详情
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //分享
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                share_localIntent.putExtra("android.intent.extra.TEXT", "推荐你使用" + clickAppInfo.getApkName() + ",下载地址:"
                        + "http://play.google.com/store/apps/details?id" + clickAppInfo.getAppPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                popupWindowDismiss();
                break;
            //启动
            case R.id.ll_start:
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getAppPackageName());
                this.startActivity(start_localIntent);
                popupWindowDismiss();
                initData();
                break;
            //卸载
            case R.id.ll_uninstall:
                //Intent uninstall_localIntent=new Intent("android.intent.action.DELETE", Uri.parse("package:"+clickAppInfo.getAppPackageName()));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + clickAppInfo.getAppPackageName()));
                startActivity(intent);
                popupWindowDismiss();
                break;
            //详情
            case R.id.ll_detail:
                //跳转到系统的设置页面
                /*Intent detail_intent = new Intent();
                detail_intent.setClassName("com.android.settings","com.android.settings.SubSettings");
                startActivity(detail_intent);*/
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getAppPackageName()));
                startActivity(detail_intent);
                popupWindowDismiss();
                break;
        }
    }

    class AppManagerAdapter extends BaseAdapter {

        private viewHolder holder;

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = userAppInfos.size() + 2;
                appInfo = systemAppInfos.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                return textView;
            }

            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position-1 );
            } else {
                int location = userAppInfos.size() + 2;
                appInfo = systemAppInfos.get(position - location);
            }


            if (convertView != null && convertView instanceof LinearLayout) {

                holder = (viewHolder) convertView.getTag();

            } else {


                convertView = View.inflate(AppManagerActivity.this, R.layout.items_app_manager, null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_appName = (TextView) convertView.findViewById(R.id.tv_appName);
                holder.tv_appSize = (TextView) convertView.findViewById(R.id.tv_appSize);
                holder.tv_appRom = (TextView) convertView.findViewById(R.id.tv_appRom);
                convertView.setTag(holder);
            }

            holder.iv_icon.setBackground(appInfo.getIcon());
            holder.tv_appName.setText(appInfo.getApkName());
            holder.tv_appSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                holder.tv_appRom.setText("手机内存");
            } else {
                holder.tv_appRom.setText("SD卡内存");
            }
            return convertView;
        }
    }


    @Override
    protected void onDestroy() {
        //在点返回键时，将弹窗销毁
        popupWindowDismiss();
        super.onDestroy();
    }

    private void popupWindowDismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    static class viewHolder {
        private ImageView iv_icon;
        private TextView tv_appName;
        private TextView tv_appSize;
        private TextView tv_appRom;

    }
}
