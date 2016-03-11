package cn.itcast.lost.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.adapter.MyBaseAdapter;
import cn.itcast.lost.bean.AppInfo;
import cn.itcast.lost.bean.TaskInfo;
import cn.itcast.lost.engine.TaskInfoParser;
import cn.itcast.lost.utils.SharePreferencesUtils;
import cn.itcast.lost.utils.SystemInfoUtils;
import cn.itcast.lost.utils.ToastUtils;
import cn.itcast.safe.R;

public class TaskManagerActivity extends Activity {

    @ViewInject(R.id.tv_task_progress)
    private TextView tv_task_progress;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.list_view)
    private ListView list_view;
    @ViewInject(R.id.ll_pt)
    private LinearLayout ll_pt;

    List<TaskInfo> taskInfos;
    ArrayList<TaskInfo> userAppInfos;
    ArrayList<TaskInfo> systemAppInfos;
    TaskMangagerAdapter taskMangagerAdapter;

    private int processCount;
    private long availMem;
    private long totalMem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initUi();
        initData();
    }

    /**
     * ActivityMangager和packageMangager的区别
     * ActivityMangager（活动管理器）
     * packageMangager（包管理器）
     */
    private void initUi() {
        ViewUtils.inject(this);

        ll_pt.setVisibility(View.VISIBLE);

        //获取进程的个数（通过工具类）
        processCount = SystemInfoUtils.getProcessCount(this);
        tv_task_progress.setText("进程" + processCount + "个");

        //获得可用的内存大小（通过工具类）
        availMem = SystemInfoUtils.getAvailMem(this);

        //通过工具类获得总内存大小（通过工具类）
        totalMem = SystemInfoUtils.getTotalMem(this);

        tv_task_memory.setText("剩余/总内存:" + Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this, totalMem));

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前Item的对象
                Object object = list_view.getItemAtPosition(position);

                if (object != null && object instanceof TaskInfo) {
                    //转换对象
                    TaskInfo clickTaskInfo = (TaskInfo) object;

                    //拿到已存在的view.getTag(重要）
                    viewHolder holder = (viewHolder) view.getTag();
                    if (clickTaskInfo.getPackageName().equals(getPackageName())) {
                        return;
                    }
                    if (clickTaskInfo.isChecked()) {
                        clickTaskInfo.setChecked(false);
                        holder.cb_stop.setChecked(false);
                    } else {
                        clickTaskInfo.setChecked(true);
                        holder.cb_stop.setChecked(true);

                    }
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                //将所有app的集合分成===用户app集合+系统app
                userAppInfos = new ArrayList<TaskInfo>();
                systemAppInfos = new ArrayList<TaskInfo>();

                for(TaskInfo taskInfo:taskInfos){
                    if(taskInfo.isUserApk()){
                        userAppInfos.add(taskInfo);
                    }else {
                        systemAppInfos.add(taskInfo);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskMangagerAdapter = new TaskMangagerAdapter();
                        list_view.setAdapter(taskMangagerAdapter);
                        ll_pt.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    /**
     * 因为勾选完显示系统进程之后，后退以后就要让此界面将系统进程
     * 显示出来，那么就要了解Activity的生命周期，因此在这个实现在这个里面进行
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(taskMangagerAdapter!=null){
            taskMangagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 适配器
     */
    class TaskMangagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            //如果选中要显示系统进程那就显示，如果不就只显示用户进程
            if(SharePreferencesUtils.getBoolean(TaskManagerActivity.this,"is_show",false)){
                return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
            }else{
                return userAppInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo;

            if (position < userAppInfos.size() + 1) {
                taskInfo = userAppInfos.get(position - 1);
            } else {
                int location = userAppInfos.size() + 2;
                taskInfo = systemAppInfos.get(position - location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                return textView;
            }

            TaskInfo taskInfo;

            if (position < (userAppInfos.size() + 1)) {
                taskInfo = userAppInfos.get(position-1 );
            } else {
                int location = userAppInfos.size() + 2;
                taskInfo = systemAppInfos.get(position - location);
            }

            viewHolder holder;
            View view;
            if(convertView!=null && convertView instanceof LinearLayout){
                view=convertView;
                holder= (viewHolder) view.getTag();
            }else {
                view = View.inflate(TaskManagerActivity.this, R.layout.items_task_manager, null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_taskName = (TextView) view.findViewById(R.id.tv_taskName);
                holder.tv_taskRom = (TextView) view.findViewById(R.id.tv_taskRom);
                holder.cb_stop = (CheckBox) view.findViewById(R.id.cb_stop);
                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_taskName.setText(taskInfo.getAppName());
            holder.tv_taskRom.setText("内存大小:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));

            //将自己的程序进程隐藏掉
            if(taskInfo.getPackageName().equals(getPackageName())){
                holder.cb_stop.setVisibility(View.INVISIBLE);
            }else {
                holder.cb_stop.setVisibility(View.VISIBLE);
            }

            //判断是否处于勾选状态
            if(taskInfo.isChecked()){
               holder.cb_stop.setChecked(true);
           }else {
               holder.cb_stop.setChecked(false);
           }
            return view;
        }
    }



    /**
     * 全选按键的响应事件
     * @param view
     */
    public void selectAll(View view){

        for(TaskInfo taskInfo:userAppInfos){
            //先判断是不是自己程序本身，如果是本身的话，就不勾选
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }

        for(TaskInfo taskInfo:systemAppInfos){
            taskInfo.setChecked(true);
        }

        //一旦数据发生改变就一定要刷新界面
        taskMangagerAdapter.notifyDataSetChanged();

    }

    /**
     * 全部按键取消响应
     * @param view
     */
    public void unSelectAll(View view){

        for(TaskInfo taskInfo:userAppInfos){
            //先判断是不是自己程序本身，如果是本身的话，就不勾选
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(false);
        }

        for(TaskInfo taskInfo:systemAppInfos){
            taskInfo.setChecked(false);
        }

        //一旦数据发生改变就一定要刷新界面
        taskMangagerAdapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     * @param view
     */
    public void killProcess(View view){

        //想杀死进程，首先必须得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //清理总共的进程个数
        int totalCount=0;
        //清除了多少进程大小
        int killMem=0;

        List<TaskInfo> killTaskInfos = new ArrayList<>();

        /**
         * 迭代用户进程
         * 此处需注意在迭代的时候不能将元素移除，也就是不能改变集合的大小
         * 所以需要将要删除的元素放在另外一个空集合内过后
         */

        for(TaskInfo taskInfo:userAppInfos){
            if(taskInfo.isChecked()){
                /**杀死进程参数表示包名,此处杀死进程需要权限
                 *<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
                 */
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                totalCount++;
                killMem+=taskInfo.getMemorySize();
                killTaskInfos.add(taskInfo);
            }
        }

        //迭代系统进程
        for(TaskInfo taskInfo:systemAppInfos){
            if(taskInfo.isChecked()){
                //杀死进程参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                totalCount++;
                killMem+=taskInfo.getMemorySize();
                killTaskInfos.add(taskInfo);
            }
        }

        //对需要删除的元素进行统一删除
        for(TaskInfo taskInfo:killTaskInfos){
            if(taskInfo.isUserApk()){
                userAppInfos.remove(taskInfo);
            }else {
                systemAppInfos.remove(taskInfo);
            }
        }

        ToastUtils.showToast(this,"共清理"+totalCount+"个进程,释放"+
                Formatter.formatFileSize(this,killMem));

        //当程序被清除了以后，要改变剩余的程序个数，以及剩余内存
        tv_task_progress.setText("进程" + (processCount - totalCount) + "个");
        tv_task_memory.setText("剩余/总内存:"+ Formatter.formatFileSize(this,(availMem+killMem))+"/"+Formatter.formatFileSize(this,totalMem));

        //一旦数据发生改变就一定要刷新界面
        taskMangagerAdapter.notifyDataSetChanged();

    }

    /**
     * 打开设置选项
     * @param view
     */
    public void openSetting(View view){
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }

     static class viewHolder{

        private ImageView iv_icon;
        private TextView tv_taskName;
        private TextView tv_taskRom;
        private CheckBox cb_stop;
    }

}
