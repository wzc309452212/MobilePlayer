package com.wzc.mobileplayer;

import android.app.Application;
import org.xutils.x;

/**
 * Created by admin on 2017/8/1.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志
    }
}
