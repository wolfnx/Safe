package cn.itcast.lost.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.itcast.lost.service.AddressService;
import cn.itcast.lost.service.CallSafeService;
import cn.itcast.lost.utils.ServiceStatusUtils;
import cn.itcast.lost.view.SettingClickView;
import cn.itcast.lost.view.SettingItemView;
import cn.itcast.safe.R;


/**
 * 设置中心
 * @author wolfnx
 *
 */

public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;//设置升级
	private SettingItemView sivAddress;//设置归属地
	private SettingClickView sivAddressStyle;//设置显示框的风格
	private SettingClickView sivAddressLocation;//设置显示框的位置
	private SettingItemView sivCallSafe;//设置黑名单
	private SharedPreferences mPref;
	final String [] items=new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mPref = getSharedPreferences("lost", MODE_PRIVATE);

		autoUpdate();
		initAddressView();
		initCallSafeView();
		initAddressStyle();
		initAddressLocation();
	}

	/**
	 * 初始化黑名单
	 */
	private void initCallSafeView() {

		sivCallSafe=(SettingItemView) findViewById(R.id.siv_callSafe);

		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "cn.itcast.lost.service.CallSafeService");

		//根据服务是否开启来决定显示
		if(serviceRunning){
			sivCallSafe.setCheck(true);
		}else{
			sivCallSafe.setCheck(false);
		}
		sivCallSafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(sivCallSafe.isChecked()){
					sivCallSafe.setCheck(false);
					stopService(new Intent(SettingActivity.this,CallSafeService.class));//停止归属地服务
				}else{
					sivCallSafe.setCheck(true);
					startService(new Intent(SettingActivity.this,CallSafeService.class));//开启归属地服务
				}
			}
		});
	}

	/**
	 * 自动更新升级开关
	 */
	private void autoUpdate(){

		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		//sivUpdate.setTitle("自动更新设置");

		boolean autoUpdate=mPref.getBoolean("auto_update", true);

		if(autoUpdate){
			//sivUpdate.setDesc("自动更新已开启");
			sivUpdate.setCheck(true);
		}else{
			//sivUpdate.setDesc("自动更新已关闭");
			sivUpdate.setCheck(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(sivUpdate.isChecked()){
					sivUpdate.setCheck(false);
					//sivUpdate.setDesc("自动更新已关闭");
					//更新sp
					mPref.edit().putBoolean("auto_update",false).commit();
				}else{
					sivUpdate.setCheck(true);
					//sivUpdate.setDesc("自动更新已开启");
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}


	/**
	 * 初始化归属地开关
	 */
	private void initAddressView(){
		sivAddress=(SettingItemView) findViewById(R.id.siv_address);

		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "cn.itcast.lost.service.AddressService");

		//根据服务是否开启来决定显示
		if(serviceRunning){
			sivAddress.setCheck(true);
		}else{
			sivAddress.setCheck(false);
		}
		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(sivAddress.isChecked()){
					sivAddress.setCheck(false);
					stopService(new Intent(SettingActivity.this,AddressService.class));//停止归属地服务
				}else{
					sivAddress.setCheck(true);
					startService(new Intent(SettingActivity.this,AddressService.class));//开启归属地服务
				}
			}
		});
	}
	
	private void initAddressStyle(){
		sivAddressStyle=(SettingClickView) findViewById(R.id.siv_addressStyle);
		sivAddressStyle.setTitle("归属地提示框风格");
		int style=mPref.getInt("addressStyle",0);	
		
		sivAddressStyle.setDesc(items[style]);
		sivAddressStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				showSingleChooseDailog();
			}
		});
	}
	/**
	 * 弹出选择风格的单选框
	 */
	protected void showSingleChooseDailog() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示框风格");
		
		int style=mPref.getInt("addressStyle",0);
		
		builder.setSingleChoiceItems(items,style,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPref.edit().putInt("addressStyle",which).commit();
				dialog.dismiss();//让Dialog消失
				sivAddressStyle.setDesc(items[which]);//更新控件显示
			}
		});
		
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 设置归属地提示框的位置
	 */
	private void initAddressLocation(){
		
		sivAddressLocation=(SettingClickView) findViewById(R.id.siv_addressLocation);
		sivAddressLocation.setTitle("归属地提示框位置");
		sivAddressLocation.setDesc("设置归属地提示框的位置");
		
		sivAddressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
			}
		});
	}
}
