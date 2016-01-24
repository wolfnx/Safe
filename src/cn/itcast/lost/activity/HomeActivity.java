package cn.itcast.lost.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.itcast.lost.utils.Md5Utils;
import cn.itcast.safe.R;


/**
 * 主页面
 *
 * @author wolfnx
 */
public class HomeActivity extends Activity {

    private GridView gvHome;
    private String[] mItem = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getsharepreference
        mPref = getSharedPreferences("lost", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        //设置监听
        gvHome.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        showPasswordDialog();
                        break;
                    case 1:
                        //通讯卫士
                        startActivity(new Intent(HomeActivity.this, CallSafeActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(new Intent(HomeActivity.this, AToolsActivity.class));
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     *
     * @return
     * @author wolfnx
     */
    private void showPasswordDialog() {
        //判断是否设置过密码
        String savedPassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            //如果设置过了，那就弹出验证密码的弹出框
            showPasswordInputDialog();
        } else {
            //如果没有设置过，弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }

    /**
     * 设置验证密码的弹出框
     */
    private void showPasswordInputDialog() {
        //创建弹框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();//创建Dialog对象

        View view = View.inflate(this, R.layout.dailog_input_password, null);
        //dialog.setView(view);//将自定义的布局文件给dialog
        dialog.setView(view, 0, 0, 0, 0);//设置边距为零，保证在2.3的系统上无问题。
        Button btnOK = (Button) view.findViewById(R.id.bt_ok);
        Button btnCancel = (Button) view.findViewById(R.id.bt_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);

        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String savedPassword = mPref.getString("password", null);
                if (!TextUtils.isEmpty(password)) {
                    if (Md5Utils.encode(password).equals(savedPassword)) {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码不正确", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();

                }


            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    /**
     * 设置密码弹窗
     *
     * @author wolfnx
     */
    private void showPasswordSetDailog() {
        //创建弹框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();//创建Dialog对象

        View view = View.inflate(this, R.layout.dailog_set_password, null);
        //dialog.setView(view);//将自定义的布局文件给dialog
        dialog.setView(view, 0, 0, 0, 0);//设置边距为零，保证在2.3的系统上无问题。
        Button btnOK = (Button) view.findViewById(R.id.bt_ok);
        Button btnCancel = (Button) view.findViewById(R.id.bt_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConform = (EditText) view.findViewById(R.id.et_password_confirm);

        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordComfirm = etPasswordConform.getText().toString();

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordComfirm)) {
                    if (password.equals(passwordComfirm)) {
                        dialog.dismiss();
                        mPref.edit().putString("password", Md5Utils.encode(password)).commit();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不为空", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItem.length;
        }

        @Override
        public Object getItem(int position) {
            return mItem[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup arg2) {
            View view = View.inflate(HomeActivity.this,
                    R.layout.home_list_item, null);
            ImageView ivItem = (ImageView) view.findViewById(R.id.iv_items);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_items);

            tvItem.setText(mItem[position]);
            ivItem.setImageResource(mPics[position]);
            return view;
        }

    }
}
