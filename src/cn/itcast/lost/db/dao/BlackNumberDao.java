package cn.itcast.lost.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.lost.bean.BlackNumberInfo;
import cn.itcast.lost.db.BlackNumberOpenHelper;

/**
 * 黑名单dao类
 * Created by wolfnx on 2016/1/22.
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper helper;
    private Cursor cursor;

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
        //为什么要延迟3s，因为现在我们用的本地数据比较快
        //而实用项目中经常要在网上获取数据，此处模拟在网上获取数据，
        // 因此布局文件中就要加缓冲的ProgressBar
        SystemClock.sleep(3000);
        return list;
    }

    /**
     * 分页加载
     * @param pageNumber 表示当前是哪一页
     * @param passSize 每一页有多少数
     * @return
     * limit 表示限制当前有多少数据
     * offset 表示跳过 从几条开始
     */
    public List<BlackNumberInfo> findPar(int pageNumber,int passSize){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor= db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(passSize), String.valueOf(pageNumber * passSize)});
        ArrayList<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     *  分批加载
     * @param startIndex 开始位置
     * @param maxCount  每页展示最大的条目
     * @return
     */
    public List<BlackNumberInfo> findPart(int startIndex,int maxCount){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor= db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        ArrayList<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }



    /**
     * 获取总的记录数
     * @return
     */
    public int getTotalNumber(){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

}
