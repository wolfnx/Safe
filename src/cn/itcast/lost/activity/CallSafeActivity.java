package cn.itcast.lost.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.security.PrivateKey;
import java.util.List;

import cn.itcast.lost.adapter.MyBaseAdapter;
import cn.itcast.lost.bean.BlackNumberInfo;
import cn.itcast.lost.db.dao.BlackNumberDao;
import cn.itcast.safe.R;

public class CallSafeActivity extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUi();
        initData();
    }

    private void initData() {
        BlackNumberDao dao = new BlackNumberDao(this);
        blackNumberInfos = dao.findAll();
        CallSafeAdapter callSafeAdapter = new CallSafeAdapter(blackNumberInfos,CallSafeActivity.this);
        listView.setAdapter(callSafeAdapter);
    }

    public void initUi() {
        listView = (ListView) findViewById(R.id.list_view);
    }

    class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        private CallSafeAdapter(List lists,Context mContext){
            super(lists,mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.items_call_safe, null);
                holder = new viewHolder();
                holder.callSafeNumber = (TextView) convertView.findViewById(R.id.tv_callSafeNumber);
                holder.callSafeMode = (TextView) convertView.findViewById(R.id.tv_callSafeMode);
                convertView.setTag(holder);
            } else {
                holder=(viewHolder)convertView.getTag();
            }

           holder.callSafeNumber.setText(blackNumberInfos.get(position).getNumber());
            String mode = blackNumberInfos.get(position).getMode();
            if (mode.equals("1")) {
                holder.callSafeMode.setText("全部拦截");
            } else if (mode.equals("2")) {
                holder.callSafeMode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.callSafeMode.setText("短信拦截");
            }
            return convertView;
        }
    }

    static class viewHolder {
        TextView callSafeNumber;
        TextView callSafeMode;
    }
}
