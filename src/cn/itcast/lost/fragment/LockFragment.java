package cn.itcast.lost.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.db.dao.AppLockDao;
import cn.itcast.lost.engine.AppInfoParser;
import cn.itcast.safe.R;

public class LockFragment extends Fragment {

    private ListView listView;
    private TextView tv_lock;

    private lockAdapter lockAdapter;
    private  List<AppInfo> appInfos;
    private ArrayList<AppInfo> lockList;
    private AppLockDao dao;

    /**
     * 初始化界面
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_lock,null);
        listView=(ListView) view.findViewById(R.id.list_view);
        tv_lock=(TextView) view.findViewById(R.id.tv_lock);
        return view;
    }

    /**
     * 初始化数据
     */
    @Override
    public void onStart() {
        super.onStart();

        dao = new AppLockDao(getActivity());

        lockList = new ArrayList<AppInfo>();
        //获取手机上安装的所有程序
        this.appInfos = AppInfoParser.getAppInfos(getActivity());

        for(AppInfo appInfo: this.appInfos){
            if(dao.find(appInfo.getAppPackageName())){
                lockList.add(appInfo);
            }
        }

        lockAdapter = new lockAdapter();
        listView.setAdapter(lockAdapter);

    }

    public class lockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tv_lock.setText("加锁的应用程序"+lockList.size()+"个");
            return lockList.size();
        }

        @Override
        public Object getItem(int position) {
            return lockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            viewHolder holder;

            if(convertView!=null){
                view=convertView;
                holder=(viewHolder) view.getTag();
            }else {
                holder = new viewHolder();
                view=View.inflate(getActivity(),R.layout.items_lock_fragment,null);
                holder.iv_icon=(ImageView) view.findViewById(R.id.iv_icon);
                holder.iv_lock=(ImageView) view.findViewById(R.id.iv_lock);
                holder.tv_name=(TextView) view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }

            final AppInfo appLockInfo = lockList.get(position);
            holder.iv_icon.setImageDrawable(appLockInfo.getIcon());
            holder.tv_name.setText(appLockInfo.getApkName());

            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //创建平移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);

                    animation.setDuration(1000);

                    view.startAnimation(animation);


                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            SystemClock.sleep(1000);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //从数据库中删除该数据
                                    dao.delete(appLockInfo.getAppPackageName());
                                    //从集合中移除该元素
                                    lockList.remove(appLockInfo);

                                    lockAdapter.notifyDataSetChanged();

                                }
                            });

                        }
                    }.start();
                }
            });

            return view;
        }
    }

    static class viewHolder{

        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
}
