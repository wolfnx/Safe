package cn.itcast.lost.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 因为要经常使用sp所以将其封装起来
 * Created by wolfnx on 2016/2/27.
 */
public class SharePreferencesUtils {

    public static final String SP_NAME="lost";

    public static void saveBoolean(Context mContext,String key,boolean vlaue){
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key,vlaue).commit();
    }

    public static boolean getBoolean(Context mContext,String key,boolean defValue){
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key,defValue);
    }
}
