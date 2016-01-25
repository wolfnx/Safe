package cn.itcast.lost.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;


import cn.itcast.lost.adapter.MyBaseAdapter;
import cn.itcast.lost.bean.BlackNumberInfo;
import cn.itcast.lost.db.dao.BlackNumberDao;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

public class CallSafeActivity extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pt;

    private int mCurrentPageNumber = 1;
    private int mPageSize = 20;
    private TextView callSafeBoot;
    private int totalPage;
    private BlackNumberDao dao;
    private EditText etSafeBoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUi();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pt.setVisibility(View.INVISIBLE);
            CallSafeAdapter callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
            listView.setAdapter(callSafeAdapter);
        }
    };


    private void initData() {

        //因为以后数据可能很多，所以我们就不能用主线程，最好用子线程来实现
        new Thread() {
            @Override
            public void run() {
                super.run();
                //通过记录数/每页多少个，就可能得到到少页
                dao = new BlackNumberDao(CallSafeActivity.this);
                totalPage = dao.getTotalNumber() / mPageSize;
                //设置跳转的TextView的text值
               // callSafeBoot.setText(mCurrentPageNumber + "/" + totalPage);
                //不一次性全查了，改成分页查询
                //blackNumberInfos = dao.findAll();
                blackNumberInfos = dao.findPar(mCurrentPageNumber, mPageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    public void initUi() {

        ll_pt = (LinearLayout) findViewById(R.id.ll_pt);
        //在加载图像时将ProgressBar显示出来
        ll_pt.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.list_view);
        callSafeBoot = (TextView) findViewById(R.id.tv_callSafeBoot);
        etSafeBoot= (EditText) findViewById(R.id.et_callSafeBoot);

    }

    class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        private CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
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
                holder = (viewHolder) convertView.getTag();
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

    /**
     * 跳转到下一页
     *
     * @param view
     */
    public void callNext(View view) {
        if(mCurrentPageNumber>=(totalPage-1)){
            ToastUtils.showToast(CallSafeActivity.this,"已到达最后一页");
            return;
        }
        mCurrentPageNumber++;
        initData();

    }

    /**
     * 跳转到上一页
     *
     * @param view
     */
    public void callPre(View view) {
        if(mCurrentPageNumber<=1){
            ToastUtils.showToast(CallSafeActivity.this,"已到达起始页");
            return;
        }
        mCurrentPageNumber--;
        initData();
    }

    /**
     * 跳转到某一页
     *
     * @param view
     */
    public void callBoot(View view) {
        String pageNumber=etSafeBoot.getText().toString().trim();
        if(TextUtils.isEmpty(pageNumber)){
            ToastUtils.showToast(this,"请输入跳转页");
        }else{
            int number=Integer.parseInt(pageNumber);
            if(number>=1&&number<=totalPage){
                mCurrentPageNumber=number-1;
                initData();
            }else{
                ToastUtils.showToast(this,"请输入正确页面数");
            }
        }

    }
}
