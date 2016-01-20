package cn.itcast.lost.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.safe.R;
/**
 * 归属地提示框的位置
 * @author wolfnx 
 * 
 */
public class DragViewActivity extends Activity {
	
	private TextView tvTop;
	private TextView tvBottom;
	private ImageView ivDrag;
	protected int startY;
	protected int startX;
	private SharedPreferences mPref;
	private int winWidth;
	private int winHeight;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		
		mPref = getSharedPreferences("lost",MODE_PRIVATE);
		tvTop=(TextView) findViewById(R.id.tv_top);
		tvBottom=(TextView) findViewById(R.id.tv_bottom);
		
		
		ivDrag=(ImageView) findViewById(R.id.iv_drag);
		int lastX=mPref.getInt("lastX", 0);
		int lastY=mPref.getInt("lastY", 0);
		
		//记录上次设置的提示框位置
		//onMeasure(测量view),onLayout(安放位置),onDraw(绘制)
		//ivDrag.layout(lastX, lastY, lastX+ivDrag.getWidth(),lastY+ivDrag.getHeight());
		//因为测量未完成，所以此方法不可用	
		
		winWidth = getWindowManager().getDefaultDisplay().getWidth();
		winHeight= getWindowManager().getDefaultDisplay().getHeight();
		
		if(lastY>winHeight/2){//一边显示，一边隐藏
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		}else{
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}
		
		RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams)ivDrag.getLayoutParams();//获取布局参数
		layoutParams.leftMargin=lastX;//设置左边距
		layoutParams.topMargin=lastY;//设置上边距
		ivDrag.setLayoutParams(layoutParams);//重新设置位置
			
		ivDrag.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					//初始化起点坐标
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
				   int	endX=(int) event.getRawX();
				   int	endY=(int) event.getRawY();
					
					//计算移动偏移量
					int dx=endX-startX;
					int dy=endY-startY;
					
					//更新左上右下坐标
					int l=ivDrag.getLeft()+dx;
					int r=ivDrag.getRight()+dx;
					int t=ivDrag.getTop()+dy;
					int b=ivDrag.getBottom()+dy;
					
					//检验是否超出屏幕的边界
					if(l<0||r>winWidth||t<0||b>winHeight-20){
						break;
					}
					
					if(t>winHeight/2){//一边显示，一边隐藏
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					}else{
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}
					
					
					//更新界面
					ivDrag.layout(l, t, r, b);
					
					//重新初始化起点坐标
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor=mPref.edit();
					editor.putInt("lastX", ivDrag.getLeft());
					editor.putInt("lastY", ivDrag.getTop());
					editor.commit();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
}
