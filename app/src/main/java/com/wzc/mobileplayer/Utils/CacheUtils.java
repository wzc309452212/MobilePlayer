package com.wzc.mobileplayer.Utils;

/**
 * Created by admin on 2017/8/11.
 */
import android.content.Context;
import android.content.SharedPreferences;

import com.wzc.mobileplayer.Service.MusicPlayerService;
public class CacheUtils {
    public static void putString(Context mContext,String key,String value){
        SharedPreferences sp = mContext.getSharedPreferences("wzc",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    /**
     * 保持播放模式
     * @param context
     * @param key
     * @param value
     */
    public static void setPlaymode(Context context,String key,int value){
        SharedPreferences sp = context.getSharedPreferences("wzc",Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }

    /**
     * 得到保存播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlaymode(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("wzc",Context.MODE_PRIVATE);
        return sp.getInt(key,MusicPlayerService.REPEAT_NORMAL);
    }
}
