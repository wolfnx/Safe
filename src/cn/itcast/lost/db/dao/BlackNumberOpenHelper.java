package cn.itcast.lost.db.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wolfnx on 2016/1/22.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper{


    public BlackNumberOpenHelper(Context context) {
        super(context,"Safe.db",null,1);
    }

    /**
     * blacknumber 表示表名
     * _id 主键自动增长
     * number 表示电话号码
     * mode 表示拦截模式
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
