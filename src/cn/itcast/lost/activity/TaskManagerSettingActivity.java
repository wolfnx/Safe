package cn.itcast.lost.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.itcast.lost.service.KillProcessService;
import cn.itcast.lost.utils.ServiceStatusUtils;
import cn.itcast.lost.utils.SharePreferencesUtils;
import cn.itcast.safe.R;

/**
 * 任务管理设置界面
 */
public class TaskManagerSettingActivity extends Activity {

    @ViewInject(R.id.ll_ls)
    private LinearLayout ll_ls;
    @ViewInject(R.id.ll_clear)
    private  LinearLayout ll_clear;
    @ViewInject(R.id.cb_clear)
    private CheckBox cb_clear;
    @ViewInject(R.id.cb_viewSystem)
    private CheckBox cb_viewSystem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        //0表示是私有的
        //sp = getSharedPreferences("lost", 0);
        initUi();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        ViewUtils.inject(this);

        //从sp中获取到底是否以前勾选过显示系统进程
        //cb_viewSystem.setChecked(sp.getBoolean("is_show",false));
        cb_viewSystem.setChecked(SharePreferencesUtils.getBoolean(this, "is_show", false));

        //从sp中获取到底是否勾选过定时清理
        //cb_clear.setChecked(sp.getBoolean("is_clear",false));
        cb_clear.setChecked(SharePreferencesUtils.getBoolean(this, "is_clear", false));


        ll_ls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status=cb_viewSystem.isChecked();
                SharePreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show", !status);
                cb_viewSystem.setChecked(!status);
                /**
                 * 用上面的简单语句将其替换
                if(cb_viewSystem.isChecked()){
                    SharePreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show", false);
                    cb_viewSystem.setChecked(false);
                }else {
                    SharePreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show", true);
                    cb_viewSystem.setChecked(true);
                }*/
            }
        });

        //定时清理进程
        final Intent intent = new Intent(this, KillProcessService.class);

        ll_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_clear.isChecked()){
                    SharePreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_clear", false);
                    cb_clear.setChecked(false);
                    stopService(intent);
                }else {
                    SharePreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_clear", true);
                    cb_clear.setChecked(true);
                    startService(intent);
                }

            }
        });
    }

    /**
     * 判断进程有没有在后台开启
     */
    @Override
    protected void onStart() {
        super.onStart();
       if(ServiceStatusUtils.isServiceRunning(this,"cn.itcast.lost.service.KillProcessService")){
           cb_clear.setChecked(true);
       }else {
           cb_clear.setChecked(false);
       }
    }
}
