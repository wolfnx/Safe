package cn.itcast.lost.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;


import cn.itcast.lost.adapter.MyBaseAdapter;
import cn.itcast.lost.bean.BlackNumberInfo;
import cn.itcast.lost.db.dao.BlackNumberDao;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

/**
 * 通讯卫士
 */
public class CallSafeActivity extends Activity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pt;
    private CallSafeAdapter callSafeAdapter;
    //开始的位置
    private int mStartIndex = 0;
    //每页展示20条数据
    private int  maxCount= 20;
    private BlackNumberDao dao;
    private int totalNumber;



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
            //为什么要做判断？因为向上滑动的时候追加完数据总是new的话就会总跳到最开始
            if(callSafeAdapter==null){
                callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                listView.setAdapter(callSafeAdapter);
            }
                callSafeAdapter.notifyDataSetChanged();
        }
    };


    private void initData() {

        //因为以后数据可能很多，所以我们就不能用主线程，最好用子线程来实现
        new Thread() {
            @Override
            public void run() {
                super.run();
                dao = new BlackNumberDao(CallSafeActivity.this);
               // 获取总的条目数
                totalNumber = dao.getTotalNumber();
                //设置跳转的TextView的text值
               // callSafeBoot.setText(mCurrentPageNumber + "/" + totalPage);
                //不一次性全查了，改成分页查询
                //blackNumberInfos = dao.findAll();
                //分批加载数据
                //如果还没加载过就是空
                if(blackNumberInfos==null){
                    blackNumberInfos = dao.findPart(mStartIndex, maxCount);
                }else {
                    blackNumberInfos.addAll(dao.findPart(mStartIndex, maxCount));//追加数据，而不是覆盖
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    public void initUi() {

        ll_pt = (LinearLayout) findViewById(R.id.ll_pt);
        //在加载图像时将ProgressBar显示出来
        ll_pt.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.list_view);
        //设置ListView滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
           //状态改变时候回调的方法
            /**
             *
             * @param view
             * @param scrollState 表示滚动的状态
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //获取到最后一条显示的数据
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        mStartIndex+=maxCount;
                        if(lastVisiblePosition==blackNumberInfos.size()-1){
                            mStartIndex+=maxCount;
                            if(lastVisiblePosition>totalNumber-2){
                                ToastUtils.showToast(CallSafeActivity.this,"已经没有数据了");
                                return;
                            }
                            initData();
                        }
                        break;
                }
            }
            //滚动时时回调的方法
            //只要手指触摸屏幕就调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
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
                holder.callDelete= (ImageView) convertView.findViewById(R.id.iv_delete);
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
            //获取当前的条目数，然后删除
            final BlackNumberInfo info=lists.get(position);
            holder.callDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        ToastUtils.showToast(CallSafeActivity.this, "删除成功");
                        lists.remove(info);
                        callSafeAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast(CallSafeActivity.this, "删除失败");
                    }
                }
            });

            return convertView;
        }
    }
    /**
     * 增加黑名单
     * @param view
     */
    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity.this);
        final AlertDialog alertDialog=builder.create();
        View inflate = View.inflate(CallSafeActivity.this, R.layout.dialog_add_black_number, null);
        final EditText blackNumber = (EditText) inflate.findViewById(R.id.et_blackNumber);
        final CheckBox call = (CheckBox) inflate.findViewById(R.id.cb_call);
        final CheckBox msg = (CheckBox) inflate.findViewById(R.id.cb_msg);

        Button bOK = (Button) inflate.findViewById(R.id.bt_ok);
        Button bCancel = (Button) inflate.findViewById(R.id.bt_cancel);

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=blackNumber.getText().toString().trim();
                if(TextUtils.isEmpty(number)){
                    ToastUtils.showToast(CallSafeActivity.this,"请输入黑名单号码");
                    return;
                }
                String mode="";
                if(call.isChecked()&&msg.isChecked()){
                    mode="1";
                }else if(call.isChecked()){
                    mode="2";
                }else if(msg.isChecked()){
                    mode="3";
                }else {
                    ToastUtils.showToast(CallSafeActivity.this,"请勾选拦截模式");
                    return;
                }
                //把号码,和拦截模式添加到黑名单
                dao.add(number,mode);
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(number);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(0,blackNumberInfo);
                if(callSafeAdapter==null){
                    callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                    listView.setAdapter(callSafeAdapter);
                }else {
                    callSafeAdapter.notifyDataSetChanged();
                }
                alertDialog.dismiss();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(inflate);
        alertDialog.show();
    }

    static class viewHolder {
        TextView callSafeNumber;
        TextView callSafeMode;
        ImageView callDelete;
    }

}
