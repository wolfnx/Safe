package cn.itcast.lost.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询工具
 * @author wolfnx
 *
 */
public class AddressDao {
	
	private static final String PATH="data/data/cn.itcast.safe/files/address.db";//必须是data/data/这个路径否则访问不到
	
	public static String getAddress(String number){
		
		String address="未知号码";
		
		//获取数据库对象
		SQLiteDatabase database=SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		
		//正则表达式电话号码：1+[3、4、5、6、7、8]+(9位)
		if(number.matches("^1[3-8]\\d{9}$")){
			//数据查询
			Cursor cursor=database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{number.substring(0, 7)});
			
			if(cursor.moveToNext()){
				address=cursor.getString(0);
			}
			cursor.close();
		}else if(number.matches("^\\d+$")){//匹配数字
			switch (number.length()){
			case 3:
				address="报警电话";
				break;
			case 4:
				address="模拟器";
				break;
			case 5:
				address="客服电话";
				break;
			case 7:
			case 8:
				address="本地号码";
				break;
			default:
				if(number.startsWith("0")&&number.length()>10){//有可能是长途电话
					//有些区号是3位，有些区号是4位
					Cursor cursor=database.rawQuery("select location from data2 where area=?)", new String[]{number.substring(1, 4)});
					if(cursor.moveToNext()){
						address=cursor.getString(0);
					}else{
						cursor.close();
						cursor=database.rawQuery("select location from data2 where area=?)", new String[]{number.substring(1, 3)});
						if(cursor.moveToNext()){
							address=cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			}	
		}
		database.close();
		return address;
	}
}
