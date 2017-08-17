package com.wzc.mobileplayer.Utils;

import android.content.Context;

/**
 * Created by admin on 2017/8/17.
 */

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dip 的单位转成为 px(像素）
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale + 0.5f);
    }



}
