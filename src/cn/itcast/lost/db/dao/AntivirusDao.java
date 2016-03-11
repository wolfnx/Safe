package cn.itcast.lost.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库查询工具类
 * Created by wolfnx on 2016/2/29.
 */
public class AntivirusDao {

    private static final String PATH="data/data/cn.itcast.safe/files/antivirus.db";//必须是data/data/这个路径否则访问不到
    /**
     * 检查当前的MD5值是不是在病毒数据库内
     * @param Md5
     * @return
     */
    public static String checkFileVirus(String Md5){

        String desc=null;

        //获取数据库对象
        SQLiteDatabase database=SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        //数据传过来的MD5值是否在病毒数据库里面
        Cursor cursor=database.rawQuery("select desc from datable where md5=?", new String[]{Md5});
        //判断游标是否可以移动
        if(cursor.moveToNext()){
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }

    /**
     * 添加服务器的Md5的值到本地的数据库
     * @param Md5 特征码
     * @param desc 描述信息
     * @return
     */
    public static void addUpdateAntivirus(String Md5,String desc){

        //获取数据库对象
        SQLiteDatabase database=SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);

        //判断是否已经更新过病毒库了
        String result = checkFileVirus(Md5);
        if(result!=null){
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("md5",Md5);
        contentValues.put("type",6);
        contentValues.put("name","Antivirus");
        contentValues.put("desc",desc);
        //增加
        database.insert("datable", null, contentValues);

    }
}
