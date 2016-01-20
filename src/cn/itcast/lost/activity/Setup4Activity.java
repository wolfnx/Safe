package cn.itcast.lost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.itcast.safe.R;

/**
 * 向导第四页
 * @author wolfnx
 *
 */
public class Setup4Activity extends BaseSetupActivity{

	private CheckBox protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		boolean openProtect = mPref.getBoolean("OpenProtect", false);
		
		protect=(CheckBox) findViewById(R.id.cb_protect);
		if(openProtect){
			protect.setChecked(true);
			protect.setText("防盗保护已开启");
		}else{
			protect.setChecked(false);
			protect.setText("防盗保护已关闭");
		}
		//当checkbox发生变化时调此方法
		protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(protect.isChecked()){
					protect.setText("防盗保护已开启");
					mPref.edit().putBoolean("OpenProtect", true).commit();
				}else{
					protect.setText("防盗保护已关闭");
					mPref.edit().putBoolean("OpenProtect", false).commit();
				}
			}
		});
		
		
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this,Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
		
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this,LostFindActivity.class));
		mPref.edit().putBoolean("configed", true).commit();
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}	
		
}

