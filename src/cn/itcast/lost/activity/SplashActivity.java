package cn.itcast.lost.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.lost.utils.StreamUtils;
import cn.itcast.safe.R;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {



	protected static final int CODE_UPDATE_DIALOG = 1;
	protected static final int CODE_URL_ERROR = 0;
	protected static final int CODE_NET_ERROR = 3;
	protected static final int CODE_JSON_EROOR = 4;
	protected static final int CODE_ENTER_HOME = 5;//进入主页面
	
	private TextView tvVersion;
	private TextView tvProgress;//显示下载进度
	//服务器版本信息
	private String mVersionName;
	private int mVersionCode;
	private String mDescr;
	private String mUrl;

	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case CODE_JSON_EROOR:
				Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			default:
				break;
			}
		};
	};
	private SharedPreferences mPref;
	private RelativeLayout rlRoot;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		tvVersion=(TextView) findViewById(R.id.tv_version);
		tvProgress=(TextView) findViewById(R.id.tv_progress);
		tvVersion.setText("版本:"+getVersionName());
		
		rlRoot= (RelativeLayout) findViewById(R.id.rl_root);
		
		mPref = getSharedPreferences("lost",MODE_PRIVATE);
		
		copyDB("address.db");//拷贝归属地查询数据库
		
		//判断是否需要更新
		boolean autoUpate = mPref.getBoolean("auto_update", true);
		
		
		if(autoUpate){
			checkVersion();
		}else{
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}
		
		//设置渐变的动画效果
		AlphaAnimation anim=new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);
	}

	public int getVersionCode(){

		/**
		 * 获取版本号		
		 */
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	private String getVersionName(){

		/**
		 * 获取版本名称
		 */
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void checkVersion(){

		/**
		 * 用子线程获取版本更新信息
		 */

		new Thread(){

			//获取线程启动开始的时间
			long starttime=System.currentTimeMillis();
			@Override
			public void run() {

				Message msg=Message.obtain();
				System.out.println(msg);
				HttpURLConnection conn=null;
				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn=(HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					conn.connect();

					int responseCode=conn.getResponseCode();
					if(responseCode==200){
						InputStream inputStream = conn.getInputStream();
						String result=StreamUtils.readFromStream(inputStream);
						System.out.println(result);

						//解析jsonS
						JSONObject jo=new JSONObject(result);
						mVersionName=jo.getString("versionName");
						mVersionCode=jo.getInt("versionCode");
						mDescr=jo.getString("description");
						mUrl=jo.getString("downloadUrl");
						System.out.println(mDescr);
						System.out.println(mVersionCode);

						if(mVersionCode>getVersionCode()){//判断是否有版本需要更新
							msg.what=CODE_UPDATE_DIALOG;
						}else{
							//没有版本更新
							msg.what=CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					msg.what=CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what=CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what=CODE_JSON_EROOR;
					e.printStackTrace();
				}finally{
					long endTime=System.currentTimeMillis();//网络连接完成时间

					long useTime=endTime-starttime;
					//强制休眠，保证闪屏页停留2s
					if(useTime<2000){
						try {
							Thread.sleep(2000-useTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
					mHandler.sendMessage(msg);
					System.out.println(msg);
					if(conn!=null){
						conn.disconnect();//关闭网络
					}
				}
			}
		}.start();	
	}
	/**
	 * 抛出是否更新选择框
	 */
	private void showUpdateDialog(){

		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("最新版本:"+mVersionName);
		builder.setMessage(mDescr);
		//builder.setCancelable(false);用户体验太差，尽量不要使用。
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.out.println("立即更新");
				download();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				enterHome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				enterHome();
			}
		});
		builder.show();
	}
	/**
	 * 进入主页面
	 */
	private void enterHome(){
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 下载更新安装包
	 */
	private void download(){

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			
			tvProgress.setVisibility(View.VISIBLE);
			
			String target=Environment.getExternalStorageDirectory()+"/update.apk";
			//xUtils
			HttpUtils utils=new HttpUtils();
			utils.download(mUrl, target, new RequestCallBack<File>() {

				//下载文件进度
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					super.onLoading(total, current, isUploading);
					System.out.println("下载进度:"+current+"/"+total);
					tvProgress.setText("下载进度"+current*100/total+"%");
				}
				//下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("下载成功");
					//跳转到系统下载页面
					Intent intent=new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
					startActivityForResult(intent, 0);//如果用户点击取消安装，返回结果。
				}
				//下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_LONG).show();
				}
			});
		}else{
			Toast.makeText(SplashActivity.this, "你没有SD卡", Toast.LENGTH_LONG).show();
		}		
	}
	
	/**
	 * 用户取消安装后进入主页面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 拷贝数据库
	 * @param dbName
	 */
	private void copyDB(String dbName){
		
		File destFile=new File(getFilesDir(),dbName);//获取文件路径
		System.out.println(getFilesDir());
		
		if(destFile.exists()){
			System.out.println("数据库已存在！不需要再拷贝");
			return;
		}
		
		FileOutputStream out=null;
		InputStream in=null;
		
		try {
			in = getAssets().open(dbName);
			out=new FileOutputStream(destFile);
			
			int len=0;
			byte[] buffer=new byte[1024];
			
			while((len=in.read(buffer))!=-1){
				out.write(buffer,0,len);
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
