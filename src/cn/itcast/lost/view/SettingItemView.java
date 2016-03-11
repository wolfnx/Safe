package cn.itcast.lost.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.safe.R;

/**
 * 设置中心的自定义控件
 * @author wolfnx
 *
 */
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/cn.itcast.safe";
	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox cbStatus;
	private String mTitle;
	private String mDescon;
	private String mDescoff;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");//根据属性拿到值
		mDescon = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDescoff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}
	/*
	 * 初始化布局文件
	 */
	private void initView(){
		//将自定义的布局设置给当前的SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		 tvTitle = (TextView) findViewById(R.id.tv_title);
         tvDesc=(TextView) findViewById(R.id.tv_desc);
         cbStatus=(CheckBox) findViewById(R.id.cb_status);
         setTitle(mTitle);//设置标题
         
	}
	
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	
	public void setDesc(String Desc){
		tvDesc.setText(Desc);
	}
	/**
	 * 查看当前的勾选状态
	 * @return
	 */
	public boolean isChecked(){
		return cbStatus.isChecked();
	}
	
	public void setCheck(boolean check){
		cbStatus.setChecked(check);
		
		if(check){
			setDesc(mDescon);
		}else{
			setDesc(mDescoff);
		}
	}
}
