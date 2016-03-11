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

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.db.dao.AppLockDao;
import cn.itcast.lost.engine.AppInfoParser;
import cn.itcast.safe.R;

public class UnLockFragment extends Fragment {


    private View view;
    private ListView list_view;
    private TextView tv_unlock;
    private List<AppInfo> appInfos;
    private AppLockDao dao;
    private ArrayList<AppInfo> unLockList;
    private UnLockAdapter unLockAdapter;

    /**
     * 初始化界面
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_un_lock, null);

        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);
        list_view = (ListView) view.findViewById(R.id.list_view);

        return view;
    }

    /**
     * 初始化数据，因为点击加锁和未加锁之后都会加载数据所以不能放在oncreate中
     * 加载数据，因为oncreate只会执行一次
     */
    @Override
    public void onStart() {
        super.onStart();
        //getActivity上下文
        appInfos = AppInfoParser.getAppInfos(getActivity());
        //获取到程序锁dao
        dao = new AppLockDao(getActivity());
        //未加锁程序集合
        unLockList = new ArrayList<>();

        for (AppInfo appInfo : appInfos) {
            //判断是否在程序锁数据库里面
            if (dao.find(appInfo.getAppPackageName())) {

            } else {
                unLockList.add(appInfo);
            }
        }

        unLockAdapter = new UnLockAdapter();
        list_view.setAdapter(unLockAdapter);

    }

    /**
     * 适配器
     */
    private class UnLockAdapter extends BaseAdapter {

        viewHolder holder;

        @Override
        public int getCount() {
            tv_unlock.setText("未加锁应用程序" + unLockList.size() + "个");
            return unLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            final AppInfo appInfo;

            if (convertView != null) {
                view = convertView;
                holder = (viewHolder) view.getTag();
            } else {

                holder = new viewHolder();
                view = View.inflate(getActivity(), R.layout.items_unlock_fragment, null);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                view.setTag(holder);
            }
            appInfo = unLockList.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());

            //把程序添加到程序锁数据库里面
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /**
                     * 如果动画和移除不在同一线程就会分开执行
                     * 此时就要将移除的代码延时之后再执行
                     */
                    //初始化一个位移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                            1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    //设置动画时间
                    animation.setDuration(1000);
                    //开始动画
                    view.startAnimation(animation);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //添加到数据库
                                    dao.add(appInfo.getAppPackageName());
                                    //从当前页面移除
                                    unLockList.remove(position);
                                    unLockAdapter.notifyDataSetChanged();

                                }
                            });
                        }
                    }.start();

                }
            });

            return view;
        }

    }

    static class viewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
