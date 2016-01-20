package cn.itcast.lost.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
/**
 * 设置引导页的基类，不需要在清单文件中注册，因为不需要展示。
 * @author wolfnx
 *
 */
public abstract class BaseSetupActivity extends Activity {
	
	private GestureDetector mDectector;
	public SharedPreferences mPref;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("lost",MODE_PRIVATE);
		//手势识别器
		mDectector = new GestureDetector(this,new SimpleOnGestureListener(){
			/**
			 * e1表示滑动的起点，e2表示滑动的终点
			 * velocity x表示水平速率
			 * velocity y表示垂直速率
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {	
				
				if(Math.abs(velocityX)<100){
					return true;
				}
				
				//向右划，跳上页
				if(e2.getRawX()-e1.getRawX()>150){
					showPreviousPage();
					return true;
				}
				
				if(e1.getRawX()-e2.getRawX()>150){
					showNextPage();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		
	}
	//点击按钮跳到上一页
	public void previous(View v){
		showPreviousPage();
	}
	//点击按钮跳到下一页
	public void next(View v){
		showNextPage();
	}
	
	//实现手势左右划
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDectector.onTouchEvent(event);//委托给手势识别器
		return super.onTouchEvent(event);
	}
	/*
	 * 强制子类必须去实现
	 */
	//展示上一页
	public abstract void showPreviousPage();
	//展示下一页
	public abstract void showNextPage();
}
