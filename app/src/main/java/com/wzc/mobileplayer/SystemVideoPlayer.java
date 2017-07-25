package com.wzc.mobileplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Date;

import Domain.MediaItem;
import Utils.Utils;


/**
 * Created by Administrator on 2017/5/24.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    // 重写方法 去注册（新版本AS去哪注册啊）

    private static final String TAG = SystemVideoPlayer.class.getSimpleName(); // "SystemVideoPlayerActivity"

    // 进度更新
    private static final int PROGRESS = 0;
    // 隐藏控制面板
    private static final int HIDE_MEDIA_CONTROLLER = 1;

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

    Utils utils;
    private MyBroadcastReceiver receiver;
    private GestureDetector detector;
    // 是否显示控制面板
    private boolean isShowMediaController = false;

    DateFormat df;

    /*
    播放列表数据
     */
    private ArrayList<MediaItem> mediaItems;
    int position;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"onCreate");
        setContentView(R.layout.activity_system_video_player);
        findViews();
        setListener();
        getData();
        setData();
        initData();

        // 设置控制面板
       // 第二次 我把你注释掉了 因为我已经写好了自己播放器的控制面板 嘿嘿 不用你了
       // videoview.setMediaController(new android.widget.MediaController(this));
    }

    private void initData() {
        utils = new Utils();

        // 注册监听电量变化的广播
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();

        // 监听电量变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,filter);

        // 初始化手势识别器
        detector = new GestureDetector(this,new MyGestureDetector());

        // 默认控制面板是隐藏的
        hideMediaController();
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HIDE_MEDIA_CONTROLLER:
                    hideMediaController(); // 隐藏控制面板
                    break;
                case PROGRESS:

                    // 获取最新的视频播放进度
                    int currentPosition = videoview.getCurrentPosition();
                    // 设置seekbar_video视频更新
                    seek_video.setProgress(currentPosition);
                    // 设置播放进度的时间
                    tv_currenttime.setText(utils.stringForTime(currentPosition));
                    // 设置系统的时间
                    tv_systemtime.setText(getSystemTime());
                    // 移除消息 每隔一秒重新发送
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    /*
    得到系统的时间
     */
    private String getSystemTime() {
        // 设置系统时间
        df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    private void getData() {
        // 一个地址 从一个文件发起的单个播放请求
        uri = getIntent().getData();

        // 得到播放列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);

    }

    private void setData() {
        if (mediaItems!=null && mediaItems.size()>0){
            // 根据位置获取播放视频的对象
            MediaItem mediaItem = mediaItems.get(position);
            videoview.setVideoPath(mediaItem.getData());
            tv_name.setText(mediaItem.getName());
        }else if (uri!=null) {
            // 设置播放地址
            videoview.setVideoURI(uri);
            tv_name.setText(uri.toString());
        }

        // 检测按钮状态
        checkButtonStatus();
    }

    private void checkButtonStatus() {
        // 1.判断一下视频列表
        if (mediaItems!=null && mediaItems.size()>0){
            //1.其他情况（视频总数大于3，非头非尾）设置默认
            setButtonEnable(true);

            //2.播放第0个，上一个按钮设置成灰色
            if (position==0){
                bt_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                bt_pre.setEnabled(false);
            }
            //3.播放最后一个，下一个按钮设置成灰色
            if (position==mediaItems.size()-1){
                bt_next.setBackgroundResource(R.drawable.btn_next_gray);
                bt_next.setEnabled(false);
            }
        }
        // 2.单个uri的情况
        else if (uri !=null){
            // 上一个和下一个都设置成灰色
            setButtonEnable(false);
        }
    }

    private void setButtonEnable(boolean b) {
        if (b){
            bt_pre.setBackgroundResource(R.drawable.btn_pre_selector);
            bt_next.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            bt_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            bt_next.setBackgroundResource(R.drawable.btn_next_gray);
        }
        bt_pre.setEnabled(b);
        bt_next.setEnabled(b);
    }

    private void setListener() {
        // 准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        // 播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        // 播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        // 2.设置SeekBar状态变化的监听
        seek_video.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    @Override
    public void onClick(View v) {
        if (v== btn_voice){

        } else if (v== bt_switch_player){

        } else if (v== bt_exit){

        } else if (v== bt_pre){
            setPreVideo();

        } else if (v== bt_start_pause){

            startAndPause();

        } else if (v== bt_next){
            setNextVideo();


        } else if (v== bt_switch_screen){

        }

        // 移除消息
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        // 重新发消息
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);

    }

    private void startAndPause() {
        if (videoview.isPlaying()){ // 判断是否正在播放着
            // 当前的播放状态设置成暂停
            videoview.pause();
            // 按钮状态-更换成播放状态
            bt_start_pause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            // 当前暂停状态 刚切换成播放状态
            videoview.start();
            // 按钮状态-更换成暂停状态
            bt_start_pause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    private void setNextVideo() {
        // 1、判断一下列表
        if (mediaItems !=null && mediaItems.size()>0){
            position++;
            if (position< mediaItems.size()){
                MediaItem mediaItem = mediaItems.get(position);
                // 得到视频标题
                tv_name.setText(mediaItem.getName());
                // 设置视频的播放地址
                videoview.setVideoPath(mediaItem.getData());

                // 视频列表中播放视频的位置发生变化就要检测一下按钮状态
                checkButtonStatus();
            } else {
                // 越界
                position = mediaItems.size()-1;
                // 关闭播放器
                finish();
            }
        }
    }

    private void setPreVideo() {
        // 1、判断一下列表
        if (mediaItems !=null && mediaItems.size()>0){
            position--;
            if (position>=0){
                MediaItem mediaItem = mediaItems.get(position);
                // 设置标题
                tv_name.setText(mediaItem.getName());
                // 设置播放地址
                videoview.setVideoPath(mediaItem.getData());

                // 播放列表中的视频位置变化就校验按钮状态
                checkButtonStatus();
            } else {
                // 越界
                position = 0;
            }
        }

    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            videoview.start(); // 当底层解码准备好的时候 开始播放

            //1.视频的总时长和seekbar关联起来
            int duration = mp.getDuration();
            seek_video.setMax(duration);

            // 设置总时长
            tv_duration.setText(utils.stringForTime(duration));

            // 发消息
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
            // 1.单个视频-退出播放器
            // 2.视频列表-播放下一个

            // Toast.makeText(SystemVideoPlayer.this, "播放完成了"+uri, Toast.LENGTH_SHORT).show();

            setNextVideo();
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

        Log.e(TAG,"onDestroy");

        // 是否是资源-释放孩子的
        if (receiver!=null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detector.onTouchEvent(event); // 把事件传递给手势识别器

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

        btn_voice.setOnClickListener(this);
        bt_start_pause.setOnClickListener(this);
        bt_exit.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_switch_player.setOnClickListener(this);
        bt_switch_screen.setOnClickListener(this);
    }


    /**
     * 自定义seekbar监听
     */
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 状态变化的时候回调
         * @param seekBar
         * @param progress 当前改变的进度-要拖动到的位置
         * @param fromUser 用户导致的改变 true  否则 false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser){
                // 响应用户拖动
                videoview.seekTo(progress);
            }
        }

        /**
         * 当手指按下时的回调
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            // 移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开时的回调
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 重新发送消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);

        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            // 得到电量0~100
            int level = intent.getIntExtra("level",0);

            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level<=0){
            iv_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level<=10){
            iv_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level<=20){
            iv_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level<=40){
            iv_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level<=60){
            iv_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level<=80){
            iv_battery.setImageResource(R.drawable.ic_battery_80);
        } else if (level<=100){
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            // Toast.makeText(SystemVideoPlayer.this, "我被长按了", Toast.LENGTH_SHORT).show();
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {


            Toast.makeText(SystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            //Toast.makeText(SystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
            if (isShowMediaController){
                // 隐藏
                hideMediaController();
                // 把消息移除
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            } else {
                // 显示
                ShowMediaController();
                // 重新发消息 4秒后自动隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    // 显示控制面板
    private void ShowMediaController() {
        isShowMediaController = true;
        ll_top.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.VISIBLE);
    }

    // 隐藏控制面板
    private void hideMediaController() {
        isShowMediaController = false;
        ll_top.setVisibility(View.GONE);
        ll_bottom.setVisibility(View.GONE);
    }
}
