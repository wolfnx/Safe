package cn.itcast.lost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.lost.view.SettingItemView;
import cn.itcast.safe.R;

/**
 * 向导第二页
 * @author wolfnx
 *
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView sivSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		
		sivSim= (SettingItemView) findViewById(R.id.siv_sim);
		String sim=mPref.getString("sim", null);
		if(!TextUtils.isEmpty(sim)){
			sivSim.setCheck(true);
		}else{
			sivSim.setCheck(false);
		}
			
		sivSim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sivSim.isChecked()){
					sivSim.setCheck(false);
					mPref.edit().remove("sim").commit();
				}else{
					sivSim.setCheck(true);
					TelephonyManager tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber=tm.getSimSerialNumber()+"222";//获取sim卡序列号
					mPref.edit().putString("sim", simSerialNumber).commit();
					
				}
}
		});
	}
	
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this,Setup1Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
		
	}

	@Override
	public void showNextPage() {
		String sim=mPref.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			ToastUtils.showToast(this, "必须绑定Sim卡");
			return;
		}
		startActivity(new Intent(this,Setup3Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}
}
