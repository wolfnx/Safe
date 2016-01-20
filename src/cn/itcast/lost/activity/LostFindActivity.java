package cn.itcast.lost.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itcast.safe.R;
/**
 * 手机防盗
 * @author wolfnx
 *
 */
public class LostFindActivity extends Activity {
	
	private SharedPreferences mPrefs;
	private TextView tvSafeNumber;
	private ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPrefs = getSharedPreferences("lost", MODE_PRIVATE);
	

		boolean configed = mPrefs.getBoolean("configed", false);// 判断是否进入过设置向导
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			//更新安全号码
			tvSafeNumber=(TextView)findViewById(R.id.tv_safenumber);
			String phone=mPrefs.getString("SafePhone","");
			tvSafeNumber.setText(phone);
			
			
			//获取防盗保护是否开启
			ivProtect=(ImageView) findViewById(R.id.iv_protect);
			boolean protect=mPrefs.getBoolean("OpenProtect", false);
			if(protect){
				ivProtect.setImageResource(R.drawable.lock);
			}else{
				ivProtect.setImageResource(R.drawable.unlock);
			}
			
		} else {
			// 跳转设置向导页
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}
	}
	
	public void previous(View v){
		startActivity(new Intent(this,Setup1Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
}
