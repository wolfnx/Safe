package cn.itcast.lost.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {
	//有style样式的话会走此方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	//有属性时走此方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//new对象时走此方法
	public FocusedTextView(Context context) {
		super(context);
	}
	/**
	 * 让跑马灯获得焦点强制返回true;
	 */
	@Override
	public boolean isFocused() {
		return true;
	}

}
