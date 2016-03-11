package cn.itcast.lost.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.db.AppLockOpenHelper;
import cn.itcast.lost.utils.Crypto;


/**
 * 程序锁dao类
 * Created by wolfnx on 2016/3/1.
 */
public class AppLockDao {

    private AppLockOpenHelper helper;
    private Context context;

    public AppLockDao(Context mContext) {
        context=mContext;
        helper = new AppLockOpenHelper(mContext);
    }

    /**
     * 增加
     * @param packageName
     * @return
     */
    public boolean add(String packageName){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("packageName",packageName);
        long rowid = db.insert("info", null, contentValues);

        /**
         * 自定义注册的内容观察者，所以Uri类型随便给
         */
        context.getContentResolver().notifyChange(Uri.parse("cn.itcast.lost.lock"), null);
        if(rowid==-1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 删除
     * @param packageName
     * @return
     */
    public  boolean delete(String packageName){
        SQLiteDatabase db=helper.getWritableDatabase();
        int rowNumber = db.delete("info", "packageName=?", new String[]{packageName});
        context.getContentResolver().notifyChange(Uri.parse("cn.itcast.lost.lock"), null);
        if(rowNumber==0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 查询当前应用是否在库里面
     * @param packageName
     * @return
     */
    public  boolean find(String packageName){
        boolean result=false;
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packageName"}, "packageName=?", new String[]{packageName},null,null,null);
        if (cursor.moveToNext()){
            result=true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 获取库中所有的程序名
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> list=new ArrayList<>();
        Cursor cursor = db.query("info", new String[]{"packageName"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return list;
    }
}
