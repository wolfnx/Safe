package cn.itcast.lost.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wolfnx on 2016/1/25.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> lists;
    public Context mContext;

    public MyBaseAdapter() {
    }

    public MyBaseAdapter(List<T> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
