package com.wzc.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 两秒后执行
             startMainActivity();
               //  Log.e(TAG,"当前线程名称=="+Thread.currentThread().getName());
            }
        }, 2000);

    }

    private boolean isStartMain = false;

    private void startMainActivity() {
        // 为保证主页面只进入一次
        if (!isStartMain){
            isStartMain = true;
            //1.进入主页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //2.关闭当前页面
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // 移除所有消息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        // 按下和离开
        startMainActivity();
        return super.onTouchEvent(event);
    }
}
