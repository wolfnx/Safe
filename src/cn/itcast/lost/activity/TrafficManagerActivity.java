package cn.itcast.lost.activity;

import android.net.TrafficStats;
import android.os.Bundle;
import android.app.Activity;

import cn.itcast.safe.R;

public class TrafficManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        //获取手机的上传下载流量
        long rxBytes = TrafficStats.getMobileRxBytes();
        long txBytes = TrafficStats.getMobileTxBytes();


    }

}
