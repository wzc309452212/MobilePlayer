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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/5/24.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    // 重写方法 去注册（新版本AS去哪注册啊）

    private static final String TAG = SystemVideoPlayer.class.getSimpleName(); // "SystemVideoPlayerActivity"

    private VideoView videoview;
    private Uri uri;

    // Content View Elements

    private LinearLayout ll_top;
    private TextView tv_name;
    private ImageView iv_battery;
    private TextView tv_systemtime;
    private Button btn_voice;
    private SeekBar seekbar_voice;
    private Button bt_switch_player;
    private LinearLayout ll_bottom;
    private TextView tv_currenttime;
    private SeekBar seek_video;
    private TextView tv_duration;
    private Button bt_exit;
    private Button bt_pre;
    private Button bt_start_pause;
    private Button bt_next;
    private Button bt_switch_screen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"onCreate");

        setContentView(R.layout.activity_system_video_player);

        findViews();
        setListener();
        getData();
        setData();

        // 设置控制面板
       // 第二次 我把你注释掉了 因为我已经写好了自己播放器的控制面板 嘿嘿 不用你了
        // videoview.setMediaController(new android.widget.MediaController(this));
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

    @Override
    public void onClick(View v) {
        if (v== btn_voice){

        } else if (v== bt_switch_player){

        } else if (v== bt_exit){

        } else if (v== bt_pre){

        } else if (v== bt_start_pause){

        } else if (v== bt_next){

        } else if (v== bt_switch_screen){

        }
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
           // Intent intent = new Intent(this, TestB.class);
           // startActivity(intent);
            return true;
        }


        return super.onTouchEvent(event);
    }

    private void findViews() {

        videoview = (VideoView) findViewById(R.id.videoview);

        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);
        tv_systemtime = (TextView) findViewById(R.id.tv_systemtime);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        seekbar_voice = (SeekBar) findViewById(R.id.seekbar_voice);
        bt_switch_player = (Button) findViewById(R.id.bt_switch_player);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tv_currenttime = (TextView) findViewById(R.id.tv_currenttime);
        seek_video = (SeekBar) findViewById(R.id.seek_video);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        bt_exit = (Button) findViewById(R.id.bt_exit);
        bt_pre = (Button) findViewById(R.id.bt_pre);
        bt_start_pause = (Button) findViewById(R.id.bt_start_pause);
        bt_next = (Button) findViewById(R.id.bt_next);
        bt_switch_screen = (Button) findViewById(R.id.bt_switch_screen);
    }

}
