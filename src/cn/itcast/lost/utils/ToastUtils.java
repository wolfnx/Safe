package cn.itcast.lost.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * @author wolfnx
 *
 */
public class ToastUtils {
	
	public static void showToast(Context ctx,String text){
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}
}
