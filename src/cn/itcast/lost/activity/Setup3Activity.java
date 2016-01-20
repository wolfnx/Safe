package cn.itcast.lost.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

/**
 * 向导第三页
 * @author wolfnx
 *
 */
public class Setup3Activity extends BaseSetupActivity{


	private EditText etPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		etPhone=(EditText) findViewById(R.id.et_phone);
		String safePhone=mPref.getString("SafePhone", "");
		etPhone.setText(safePhone);
	}		

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this,Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
		
	}

	@Override
	public void showNextPage() {
		
		String phone=etPhone.getText().toString().trim();//去掉空格
		if(TextUtils.isEmpty(phone)){
			ToastUtils.showToast(this, "安全号码不能为空");
			return;
		}
		System.out.println(phone);
		mPref.edit().putString("SafePhone",phone).commit();//保存安全号码
		startActivity(new Intent(this,Setup4Activity.class));
		finish();
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}	
	/**
	 * 选择联系人
	 * @param view
	 */
	public void selectcontact(View view){
		
		Intent intent=new Intent(this,ContactActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			String phone=data.getStringExtra("phone");
			phone=phone.replaceAll("-", "").replaceAll(" ", "");//替换"-"和" "
			
			etPhone.setText(phone);//把电话号码设置给输入框
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
