package cn.itcast.lost.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.engine.AppInfos;
import cn.itcast.safe.R;

public class AppManagerActivity extends Activity {

    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;
    private LinearLayout ll_pt;

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

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_pt.setVisibility(View.INVISIBLE);
            AppManagerAdapter adapter=new AppManagerAdapter();
            listView.setAdapter(adapter);

        }
    };
    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //得到手机上的所有应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //将所有app的集合分成===用户app集合+系统app
                userAppInfos=new ArrayList<AppInfo>();
                systemAppInfos=new ArrayList<AppInfo>();

                for(AppInfo appInfo:appInfos){
                    if(appInfo.isUserApp()){
                        userAppInfos.add(appInfo);//添加到用户app集合
                    }else {
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
        Long rom=Environment.getDataDirectory().getFreeSpace();
        //获取SD卡存储空间
        Long sdRom=Environment.getExternalStorageDirectory().getFreeSpace();

        tvRom.setText("内存可用:"+Formatter.formatFileSize(this,rom));
        tvSD.setText("SD卡可用:"+Formatter.formatFileSize(this,sdRom));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条目位置
             * @param visibleItemCount 一页可以展示多少个
             * @param totalItemCount 总共的条目
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               if(userAppInfos!=null&&systemAppInfos!=null){
                   if(firstVisibleItem>userAppInfos.size()){
                       //系统应用程序
                       tvApp.setText("系统程序(" + systemAppInfos.size() + ")");
                   }else{
                       //用户应用程序
                       tvApp.setText("用户程序(" + userAppInfos.size() + ")");
                   }
               }
            }
        });

    }

    class AppManagerAdapter extends BaseAdapter{

        private viewHolder holder;
        @Override
        public int getCount() {
            return userAppInfos.size()+1+systemAppInfos.size()+1;
        }

        @Override
        public Object getItem(int position) {
            if(position==0){
                return null;
            }else if(position==userAppInfos.size()+1){
                return null;
            }
            AppInfo appInfo;

            if(position<userAppInfos.size()+1){
                appInfo=userAppInfos.get(position-1);
            }else{
                int location=userAppInfos.size()+2;
                appInfo=systemAppInfos.get(position-location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(position==0){
                TextView textView=new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                return textView;
            }else if(position==userAppInfos.size()+1){
                TextView textView=new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                return textView;
            }

            AppInfo appInfo;

            if(position<userAppInfos.size()+1){
                appInfo=userAppInfos.get(position-1);
            }else{
                int location=userAppInfos.size()+2;
                appInfo=systemAppInfos.get(position-location);
            }


            if(convertView!=null && convertView instanceof LinearLayout){

                holder=(viewHolder) convertView.getTag();

            }else {

                convertView= View.inflate(AppManagerActivity.this, R.layout.items_app_manager, null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_appName = (TextView) convertView.findViewById(R.id.tv_appName);
                holder.tv_appSize = (TextView) convertView.findViewById(R.id.tv_appSize);
                holder.tv_appRom = (TextView) convertView.findViewById(R.id.tv_appRom);
                convertView.setTag(holder);
            }

            holder.iv_icon.setBackground(appInfo.getIcon());
            holder.tv_appName.setText(appInfo.getApkName());
            holder.tv_appSize.setText(Formatter.formatFileSize(AppManagerActivity.this,appInfo.getApkSize()));
            if(appInfo.isRom()){
                holder.tv_appRom.setText("手机内存");
            }else {
                holder.tv_appRom.setText("SD卡内存");
            }
            return convertView;
        }
    }
    static class viewHolder{
        private ImageView iv_icon;
        private TextView tv_appName;
        private TextView tv_appSize;
        private TextView tv_appRom;

    }

}
