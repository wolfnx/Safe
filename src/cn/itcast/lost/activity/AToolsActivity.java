package cn.itcast.lost.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

import cn.itcast.lost.utils.SmsUtils;
import cn.itcast.lost.utils.SmsUtils.BackUpCallBackSms;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

/**
 * 高级工具
 *
 * @author wolfnx
 */
public class AToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 归属地查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        startActivity(new Intent(AToolsActivity.this, AddressActivity.class));
    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void backUpSms(View view) {

        //初始化一个进度条
        final ProgressDialog progressDialog = new ProgressDialog(AToolsActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("稍安勿躁，正在备份，你等着吧！");
        //设置样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {
                boolean result= SmsUtils.backUp(AToolsActivity.this, new BackUpCallBackSms() {
                    @Override
                    public void befor(int count) {
                        progressDialog.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        progressDialog.setProgress(process);
                    }
                });
                progressDialog.dismiss();
                if (result) {
                    //非UI线程toast会挂掉，所以加looper轮巡器拿消息
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份成功");
                    Looper.loop();
                } else {
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份失败");
                    Looper.loop();
                }
            }
        }.start();
    }

    /**
     * 程序锁
     * @param view
     */
    public void appLock(View view){
        startActivity(new Intent(AToolsActivity.this, AppLockActivity.class));
    }

    /**
     * 软件推荐
     * @param view
     */
    public void appRecommend(View view){

        // 调用方式一：直接打开全屏积分墙
        // OffersManager.getInstance(this).showOffersWall();

        // 调用方式二：直接打开全屏积分墙，并且监听积分墙退出的事件onDestory
        OffersManager.getInstance(this).showOffersWall(new Interface_ActivityListener() {

            /**
             * 当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
             */
            @Override
            public void onActivityDestroy(Context context) {
                Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

