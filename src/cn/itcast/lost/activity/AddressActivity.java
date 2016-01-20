package cn.itcast.lost.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import cn.itcast.lost.db.dao.AddressDao;
import cn.itcast.safe.R;
/**
 * 归属地查询
 * @author wolfnx
 *
 */
public class AddressActivity extends Activity {
	private EditText phoneFind;
	private TextView location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		phoneFind = (EditText) findViewById(R.id.et_phoneFind);
		location = (TextView) findViewById(R.id.tv_location);
		
		//监听Edit的变化
		phoneFind.addTextChangedListener(new TextWatcher() {
			
			//文本内容发生调用
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				String address=AddressDao.getAddress(s.toString());
				location.setText(address);
			}
			//文本内容发生变化前调用
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			//文本内容发生变化后调用
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}
	/**
	 * 查询
	 * @param view
	 */
	public void numberQuery(View view){
		String phone=phoneFind.getText().toString().trim();
		if(!TextUtils.isEmpty(phone)){
			String address=AddressDao.getAddress(phone);
			location.setText(address);
		}else{
			//动画让Edit摇动起来
			Animation animation=AnimationUtils.loadAnimation(this, R.anim.shake);
			phoneFind.startAnimation(animation);
			vibrate();
		}
	}
	/**
	 * 手机震动      需要震动权限
	 */
	public void vibrate(){
		Vibrator vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);//震动2s钟
		vibrator.vibrate(new long[]{1000,2000,1000,3000}, -1);//先等待1s,震动2s,等待1s,再震动3s,-1表示只执行一次
	}
}
