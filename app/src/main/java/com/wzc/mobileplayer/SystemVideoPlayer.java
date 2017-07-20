package com.wzc.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/5/24.
 */

public class SystemVideoPlayer extends Activity {
    // 重写方法 去注册（新版本AS去哪注册啊）

    private static final String TAG = SystemVideoPlayer.class.getSimpleName(); // "SystemVideoPlayerActivity"




    private VideoView videoview;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"onCreate");

        setContentView(R.layout.activity_system_video_player);

        videoview = (VideoView) findViewById(R.id.videoview);

        setListener();
        getData();
        setData();

        // 设置控制面板
        videoview.setMediaController(new android.widget.MediaController(this));
    }

    private void getData() {
        // 得到播放地址
        uri = getIntent().getData();
    }

    private void setData() {
        if (uri!=null) {
            videoview.setVideoURI(uri);
        }
    }

    private void setListener() {
        // 准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        // 播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        // 播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            videoview.start(); // 当底层解码准备好的时候 开始播放
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 1.单个视频-退出播放器
            // 2.视频列表-播放下一个

            Toast.makeText(SystemVideoPlayer.this, "播放完成了"+uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            Intent intent = new Intent(this, TestB.class);
            startActivity(intent);
            return true;
        }


        return super.onTouchEvent(event);
    }
}
