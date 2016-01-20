package cn.itcast.lost.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import cn.itcast.lost.db.dao.AddressDao;
import cn.itcast.safe.R;
/**
 * 来电提醒
 * @author wolfnx
 *
 */
public class AddressService extends Service {

	private TelephonyManager tm;
	private MyListener myListener;
	private OutCallReceiver outCallReceiver;
	private WindowManager mWM;
	private View view;
	private SharedPreferences mPref;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mPref = getSharedPreferences("lost",MODE_PRIVATE);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myListener = new MyListener();
		tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);//监听来电的状态
		
		outCallReceiver = new OutCallReceiver();
		IntentFilter intentFilter=new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallReceiver, intentFilter);//动态注册去电receiver
	}
	
	class MyListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch(state){
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("电话铃响");
				String address=AddressDao.getAddress(incomingNumber);
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE://电话闲置状态
				if(mWM!=null&&view!=null){
					mWM.removeView(view);//将View移出window
					view=null;
				}
			default:
				break;
			}
		}
	}
	/**
	 * 去电Receiver
	 * 需要权限
	 * @author wolfnx
	 *
	 */
	class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String number=getResultData();//获取电话号码
			
			String address=AddressDao.getAddress(number);
			showToast(address);
		}
	}
	/**
	 * 自定义浮窗
	 * @param message
	 */
	public void showToast(String message){
		
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		
		final int winWidth=mWM.getDefaultDisplay().getWidth();
		int winHeight=mWM.getDefaultDisplay().getHeight();
		
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;//电话窗口、它用于电话交互(特别是呼入)、它位于所有应用程序之上
		params.setTitle("Toast");
		params.gravity=Gravity.LEFT+Gravity.TOP;//将重心设置为左上方，将(0,0)改为从左上方开始
		int lastX=mPref.getInt("lastX", 0);
		int lastY=mPref.getInt("lastY", 0);
		
		//基于左上方的偏移量
		params.x=lastX;
		params.y=lastY;
		
		//view = new TextView(this);
		view=View.inflate(this, R.layout.toast_address,null);
		
		int []bgs=new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		int style=mPref.getInt("addressStyle",0);
		view.setBackgroundResource(bgs[style]);//设置归属地弹框的背景
		
		view.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					//初始化起点坐标
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX=(int) event.getRawX();
					int	endY=(int) event.getRawY();
						
					//计算移动偏移量
					int dx=endX-startX;
					int dy=endY-startY;
					
					//更新浮窗位置
					params.x+=dx;
					params.y+=dy;
					
					//防止坐标偏里屏幕
					if(params.x<0){
						params.x=0;
					}
					if(params.y<0){
						params.y=0;
					}
					if(params.x>winWidth-view.getWidth()){
						params.x=winWidth-view.getWidth();
					}
					if(params.y>winWidth-view.getHeight()){
						params.y=winWidth-view.getHeight();
					}
					
					mWM.updateViewLayout(view, params);
					
					//重新初始化起点坐标
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor=mPref.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;
				}
				return true;
			}
		});
		
		TextView tView=(TextView) view.findViewById(R.id.tv_number);
		
		tView.setText(message);
		tView.setTextColor(Color.RED);
		mWM.addView(view, params);//将view添加到屏幕上
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(myListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(outCallReceiver);
	}

}
