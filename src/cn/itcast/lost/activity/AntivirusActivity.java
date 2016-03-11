package cn.itcast.lost.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.itcast.lost.db.dao.AntivirusDao;
import cn.itcast.lost.utils.Md5Utils;
import cn.itcast.safe.R;

public class AntivirusActivity extends Activity {

    @ViewInject(R.id.iv_scanning)
    private ImageView iv_scanning;
    @ViewInject(R.id.tv_init_virus)
    private TextView tv_init_virus;
    @ViewInject(R.id.progressBar1)
    private ProgressBar progressBar;
    @ViewInject(R.id.ll_content)
    private LinearLayout ll_content;
    @ViewInject(R.id.sv_scrollView)
    private ScrollView sv_scrollView;


    /**
     * 扫描开始、扫描中、扫描结束
     */
    private static final int BEGIN=1;
    private static final int SCANNING=2;
    private static final int FINISH=3;
    private Message message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initUi();
        initData();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        ViewUtils.inject(this);
        /**
         * 第一个参数是开始位置
         * 第二个参数是结束位置
         * 第三个参数表示参照自己
         * 初始化旋转动画
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(5000);
        //设置动画无限循环
        //rotateAnimation.setRepeatMode(Animation.INFINITE);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_scanning.startAnimation(rotateAnimation);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        new Thread(){
            @Override
            public void run() {
                super.run();

                message = Message.obtain();
                message.what = BEGIN;

                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                //返回总共有多少个应用程序
                int size = installedPackages.size();

                //设置进度条的最大值
                progressBar.setMax(size);

                int process=0;

                for(PackageInfo packageInfo:installedPackages){

                    ScanInfo scanInfo = new ScanInfo();

                    //获取所有的应用名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    //获取应用的包名
                    String packageName = packageInfo.applicationInfo.packageName;

                    scanInfo.appName=appName;
                    scanInfo.packageName=packageName;

                    //获取每个应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;

                    //使用工具类获得应用程序的MD5值
                    String fileMd5 = Md5Utils.getFileMd5(sourceDir);

                    //判断当前的fileMd5值，是不是在数据库内
                    String desc = AntivirusDao.checkFileVirus(fileMd5);

                    if(desc==null){
                        scanInfo.hasVirus=false;
                    }else {
                        scanInfo.hasVirus=true;
                    }

                    process++;

                    SystemClock.sleep(50);

                    //设置进度条扫描一条就加一
                    progressBar.setProgress(process);

                    message = Message.obtain();

                    //扫描中
                    message.what=SCANNING;

                    //将对象发送过去
                    message.obj=scanInfo;

                    handler.sendMessage(message);
                }

                message = Message.obtain();

                message.what=FINISH;

                handler.sendMessage(message);
            }
        }.start();
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BEGIN:
                    tv_init_virus.setText("初始化八核杀毒引擎");
                    break;
                case SCANNING:
                    //病毒扫描中
                    TextView textView = new TextView(AntivirusActivity.this);

                    ScanInfo scanInfo=(ScanInfo) msg.obj;

                    if(scanInfo.hasVirus){
                        textView.setTextColor(Color.RED);
                        textView.setText(scanInfo.appName + "有病毒");
                    }else{
                        textView.setTextColor(Color.BLACK);
                        textView.setText(scanInfo.appName + "扫描安全");
                    }

                    ll_content.addView(textView);

                    //自动滚动
                    sv_scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            //一直向下面进行滚动
                            sv_scrollView.fullScroll(sv_scrollView.FOCUS_DOWN);
                        }
                    });
                    break;
                case FINISH:
                    //当扫描完成要让旋转的动画停下来
                    iv_scanning.clearAnimation();
                    break;
            }

        }
    };

    /**
     * 扫描应用的的信息
     */
    static class ScanInfo{
        boolean hasVirus;
        String appName;
        String packageName;
    }


}
