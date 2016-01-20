package cn.itcast.lost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import cn.itcast.lost.service.LocationService;
import cn.itcast.safe.R;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects=(Object []) intent.getExtras().get("pdus");
		
		for(Object object:objects){
			SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) object);
			
			String originatingAddress = smsMessage.getOriginatingAddress();//获取短信号码
			String messageBody = smsMessage.getMessageBody();
			System.out.println(originatingAddress);
			System.out.println(messageBody);
			
			if("#*alarm*#".equals(messageBody)){
				
				MediaPlayer player=MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);//左右声道开到最大
				player.setLooping(true);//循环播放
				player.start();
				
				abortBroadcast();//中断短信传递，从而系统APP就接收不到短信内容了;
			}else if("#*location*#".equals(messageBody)){
				//获取经纬度坐标
				context.startService(new Intent(context,LocationService.class));//开启定位服务
				
				SharedPreferences mpref = context.getSharedPreferences("lost",Context.MODE_PRIVATE);
				
				String location=mpref.getString("Location","location...");
				
				System.out.println("location:"+location);
				abortBroadcast();//中断短信传递，从而系统APP就接收不到短信内容了;
			}else if("#*lockscreen*#".equals(messageBody)){
				
				
				abortBroadcast();//中断短信传递，从而系统APP就接收不到短信内容了;
			}else if("#*wipedata*#".equals(messageBody)){
				
				
				abortBroadcast();//中断短信传递，从而系统APP就接收不到短信内容了;
			}
			
		}
	}
}
