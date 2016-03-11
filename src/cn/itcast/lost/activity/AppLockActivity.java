package cn.itcast.lost.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.itcast.lost.fragment.LockFragment;
import cn.itcast.lost.fragment.UnLockFragment;
import cn.itcast.safe.R;

public class AppLockActivity extends FragmentActivity implements View.OnClickListener {

    @ViewInject(R.id.tv_lock)
    private TextView tv_lock;
    @ViewInject(R.id.tv_unlock)
    private TextView tv_unlock;


    private LockFragment lockFragment;
    private UnLockFragment unLockFragment;


    private FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUi();
    }

    /**
     * 初始化Ui
     */
    public void initUi() {

        ViewUtils.inject(this);
        tv_lock.setOnClickListener(this);
        tv_unlock.setOnClickListener(this);
        //获得Fragment的管理者
        supportFragmentManager = getSupportFragmentManager();

        /**
         * 开启事务
         * getFragmentManager是4.0包里的不能上下兼容
         * getSupportFragmentManager扩展包里的
         */
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();

        unLockFragment = new UnLockFragment();

        lockFragment = new LockFragment();

        transaction.replace(R.id.fl_content, unLockFragment).commit();


    }

    /**
     * 加锁和未加锁的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        FragmentTransaction ts = supportFragmentManager.beginTransaction();

        switch (v.getId()){
            case R.id.tv_unlock:
                //没加锁
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);

                ts.replace(R.id.fl_content,unLockFragment).commit();

                break;
            case R.id.tv_lock:
                //加锁
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);

                ts.replace(R.id.fl_content,lockFragment).commit();

                break;
        }
    }
}
