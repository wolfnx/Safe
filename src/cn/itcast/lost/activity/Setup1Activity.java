package cn.itcast.lost.activity;

import android.content.Intent;
import android.os.Bundle;
import cn.itcast.safe.R;

/**
 * 向导第一页
 * @author wolfnx
 *
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	@Override
	public void showNextPage() {
		startActivity(new Intent(this,Setup2Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this,HomeActivity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	}	
}
