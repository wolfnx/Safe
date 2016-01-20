package cn.itcast.lost.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.itcast.safe.R;
/**
 * 高级工具
 * @author wolfnx
 *
 */
public class AToolsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	public void numberAddressQuery(View view){
		startActivity(new Intent(AToolsActivity.this,AddressActivity.class));
	}
}
