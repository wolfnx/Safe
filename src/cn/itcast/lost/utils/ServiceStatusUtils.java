package cn.itcast.lost.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;


/**
 * 服务状态工具类
 * @author wolfnx
 *
 */
public class ServiceStatusUtils {
	
	public static boolean isServiceRunning(Context ctx ,String serviceName){
		
		ActivityManager am=(ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		
		List <RunningServiceInfo> runningService=am.getRunningServices(100);//获取系统所有正在运行的服务100个
		
		for(RunningServiceInfo runningServiceInfo:runningService){
			String className=runningServiceInfo.service.getClassName();
			System.out.println(className);
			if(className.equals(serviceName)){//服务存在
				return true;
			}
		}
		return false;
	}
}
