package cn.itcast.lost.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.safe.R;

/**
 * 设置中心的自定义控件
 * @author wolfnx
 *
 */
public class SettingClickView extends RelativeLayout {

	private TextView tvTitle;
	private TextView tvDesc;
	private String mTitle;

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		initView();
	}

	public SettingClickView(Context context) {
		super(context);
		initView();
	}
	/*
	 * 初始化布局文件
	 */
	private void initView(){
		//将自定义的布局设置给当前的SettingClickView
		View.inflate(getContext(), R.layout.view_setting_click, this);
		 tvTitle = (TextView) findViewById(R.id.tv_title);
         tvDesc=(TextView) findViewById(R.id.tv_desc);
         setTitle(mTitle);//设置标题
         
	}
	
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	
	public void setDesc(String Desc){
		tvDesc.setText(Desc);
	}
}
