package cn.itcast.lost.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.BlackNumberInfo;

/**
 * Created by wolfnx on 2016/1/22.
 */
public class BlackNumberDao {

    private  BlackNumberOpenHelper helper;

    public BlackNumberDao(Context mConetext) {
        helper=new BlackNumberOpenHelper(mConetext);
    }

    /**
     * 增加拦截号码
     * @param number 号码
     * @param mode  拦截模式
     * @return
     */
    public boolean add(String number,String mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("number",number);
        contentValues.put("mode", mode);
        long rowid = db.insert("blacknumber", null, contentValues);
        if(rowid==-1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 删除拦截号码
     * @param number 号码
     * @return
     */
    public  boolean delete(String number){
        SQLiteDatabase db=helper.getWritableDatabase();
        int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
        if(rowNumber==0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 返回一个黑名单号码的拦截模式
     * @param number 号码
     * @return
     */
    public  String findNumber(String number){
        String mode="";
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number},null,null,null);
        if (cursor.moveToNext()){
            mode=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 通过号码修改拦截模式
     * @param number 号码
     * @return
     */
    public boolean changeNumberMode(String number,String mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("mode", mode);
        int updateStatus = db.update("blacknumber", contentValues, "number=?", new String[]{number});
        if(updateStatus==0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 查询所有的黑名单
     * @return
     */
    public  List<BlackNumberInfo> findAll(){
        SQLiteDatabase db=helper.getReadableDatabase();
        List<BlackNumberInfo> list=new ArrayList<BlackNumberInfo>();
        Cursor cursor=db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfo.setNumber(cursor.getString(0));
            list.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

}