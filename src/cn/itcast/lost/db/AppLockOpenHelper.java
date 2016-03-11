package cn.itcast.lost.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁数据库
 * Created by wolfnx on 2016/3/1.
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {


    public AppLockOpenHelper(Context context) {
        super(context, "appLock.db",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info(_id integer primary key autoincrement,packageName varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
