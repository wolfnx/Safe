package cn.itcast.lost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

/**
 * 程序锁的输入密码的界面
 */
public class EnterPwdActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.et_pwd)
    private EditText et_pwd;
    @ViewInject(R.id.bt_0)
    private Button bt_0;
    @ViewInject(R.id.bt_1)
    private Button bt_1;
    @ViewInject(R.id.bt_2)
    private Button bt_2;
    @ViewInject(R.id.bt_3)
    private Button bt_3;
    @ViewInject(R.id.bt_4)
    private Button bt_4;
    @ViewInject(R.id.bt_5)
    private Button bt_5;
    @ViewInject(R.id.bt_6)
    private Button bt_6;
    @ViewInject(R.id.bt_7)
    private Button bt_7;
    @ViewInject(R.id.bt_8)
    private Button bt_8;
    @ViewInject(R.id.bt_9)
    private Button bt_9;
    @ViewInject(R.id.bt_clean_all)
    private Button bt_clean_all;
    @ViewInject(R.id.bt_delete)
    private Button bt_delete;
    @ViewInject(R.id.bt_ok)
    private Button bt_ok;

    private String packageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        initUi();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {

        ViewUtils.inject(this);


        Intent intent = getIntent();

        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }


        //为了防止系统自带的键盘弹出来，将EditText的输入类型设为空，这样就弹不出来了
        et_pwd.setInputType(InputType.TYPE_NULL);

        bt_0.setOnClickListener(this);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);
        bt_clean_all.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_ok.setOnClickListener(this);

    }

    String text;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_0:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_0.getText().toString());
                break;
            case R.id.bt_1:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_1.getText().toString());
                break;
            case R.id.bt_2:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_2.getText().toString());
                break;
            case R.id.bt_3:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_3.getText().toString());
                break;
            case R.id.bt_4:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_4.getText().toString());
                break;
            case R.id.bt_5:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_5.getText().toString());
                break;
            case R.id.bt_6:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_6.getText().toString());
                break;
            case R.id.bt_7:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_7.getText().toString());
                break;
            case R.id.bt_8:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_8.getText().toString());
                break;
            case R.id.bt_9:
                text = et_pwd.getText().toString();
                et_pwd.setText(text+bt_9.getText().toString());
                break;
            //清空
            case R.id.bt_clean_all:
                et_pwd.setText("");
                break;
            //删除
            case R.id.bt_delete:
                text = et_pwd.getText().toString();
                if(text.length()==0){
                    return;
                }
                et_pwd.setText(text.substring(0,text.length()-1));
                break;
            //确定
            case R.id.bt_ok:
                if(et_pwd.getText().toString().trim().equals("123")){
                    //如果密码正确则说明是对的，不用拦截了
                    Intent intent = new Intent();
                    //发送广播停止保护
                    intent.setAction("cn.itcast.lost.stopprotect");
                    intent.putExtra("packageName", packageName);
                    sendBroadcast(intent);
                    finish();

                }else {
                    ToastUtils.showToast(EnterPwdActivity.this,"密码错误");
                    et_pwd.setText("");
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        /**
         * 要将这句注释掉，不然会有问题
         * super.onBackPressed()
         */
        //按下返回键，进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);

    }
}
