package com.wzc.mobileplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Date;

import Utils.Utils;

/**
 * Created by Administrator on 2017/5/24.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    /*
    * 视频进度的更新
    * */
    private static final int PROGRESS = 1;
    // 重写方法 去注册（新版本AS去哪注册啊） 我也不知道怎么就好了

    private VideoView videoview;
    private Uri uri;
    private Utils utils;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView tvBattery;
    private TextView tvStystemTime;
    private Button btnVoice;
    private SeekBar seekVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoSwitchScreen;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    // 1.得到当前的视频播放进度
                    int currentPosition = videoview.getCurrentPosition();

                    // 2.SeekBar.setProgress（当前进度）
                    seekVideo.setProgress(currentPosition);
                    tvStystemTime.setText(getSystemTime());
                    //更新文本播放时间进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    // 设置系统时间，在handler中 更新文本播放进度之后


                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();
        // getData();
        setListener();
        initData();
        setData();


        // 开始自定义 不需要喽
        // 设置控制面板
        // videoview.setMediaController(new android.widget.MediaController(this));
    }

    private void setData() {
                
        if (uri!=null) {
            videoview.setVideoURI(uri);
        }
    }

    private void initData() {
        utils = new Utils();
    }

    private void setListener() {
        // 设置SeekBar状态变化的监听
        seekVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        // 准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        // 播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        // 播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 当手指滑动的时候 会引起SeekBar进度变化,会回调这个方法
            if (fromUser){
                videoview.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // 当手指触碰的时候回调这个方法
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当手指离开的时候回调这个方法
        }
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start(); // 当底层解码准备好的时候 开始播放

            // 1.视频的总时长 关联总长度
            int duration = videoview.getDuration();
            seekVideo.setMax(duration);
            // 视频总时长
            tvDuration.setText(utils.stringForTime(duration));
            // 2.发消息
            handler.sendEmptyMessage(PROGRESS);
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
           // Toast.makeText(SystemVideoPlayer.this, "播放完成了"+uri, Toast.LENGTH_SHORT).show();
            //开始播放
            videoview.start();

             //准备好的时候
             //1.视频的总播放时长和SeeKBar关联起来
               int duration = videoview.getDuration();

            // seekbarVideo.setMax(duration);
            seekVideo.setMax(duration);
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-31 20:50:48 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvBattery = (ImageView)findViewById( R.id.tv_battery );
        tvStystemTime = (TextView)findViewById( R.id.tv_stystem_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekVoice = (SeekBar)findViewById( R.id.seek_voice );
        btnSwitchPlayer = (Button)findViewById( R.id.btn_switch_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekVideo = (SeekBar)findViewById( R.id.seek_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoSwitchScreen = (Button)findViewById( R.id.btn_video_switch_screen );

        btnVoice.setOnClickListener( this );
        btnSwitchPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoSwitchScreen.setOnClickListener( this );
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-31 20:50:48 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            // Handle clicks for btnVoice
        } else if ( v == btnSwitchPlayer ) {
            // Handle clicks for btnSwitchPlayer
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
        } else if ( v == btnVideoStartPause ) {
            // Handle clicks for btnVideoStartPause
            if (videoview.isPlaying()){
                // 视频在播放-设置暂停
                // 按钮状态设置播放
                videoview.pause();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }else {
                // 视频的播放
                // 按钮状态设置暂停
                videoview.start();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }
        } else if ( v == btnVideoSwitchScreen ){
        // Handle clicks for btnVideoSwitchScreen
        }
    }

    private void getData(){
        uri = getIntent().getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
