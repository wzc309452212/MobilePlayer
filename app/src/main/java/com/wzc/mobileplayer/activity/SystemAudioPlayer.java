package com.wzc.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wzc.mobileplayer.Domain.LyricBean;
import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.IMusicPlayerService;
import com.wzc.mobileplayer.R;
import com.wzc.mobileplayer.Service.MusicPlayerService;
import com.wzc.mobileplayer.Utils.LyricParaser;
import com.wzc.mobileplayer.Utils.Utils;
import com.wzc.mobileplayer.View.LyricShowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 2017/8/8.
 */

public class SystemAudioPlayer extends Activity implements View.OnClickListener{

    private TextView tv_artist;
    private TextView tv_name;
    private TextView tv_time;
    private SeekBar seekbar_audio;
    private Button bt_audio_playmode;
    private Button bt_audio_pre;
    private Button bt_audio_start_pause;
    private Button bt_audio_next;
    private Button bt_switch_lyric;
    private ImageView iv_icon;

    private int position;
    private LyricShowView lyric_show_view;

    private MyReceiver receiver;
    /**
     * 进度更新
     */
    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private Utils utils;

    private boolean notification;


    private IMusicPlayerService service;
    private ServiceConnection conn = new ServiceConnection() {
        /**当连接服务成功后回调
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (service!=null){
                // 从列表进入
                if (!notification){
                try {
                    // 开始播放
                    service.openAudio(position);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            } else {
                    // 再次显示
                    showViewData(null);
                }
            }
        }

        /**当断开的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        startAndBindService();
    }

    /**
     * 接收广播
     */
    private void initData() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver,intentFilter);

        utils = new Utils();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);

        // 启动服务
        startService(intent); // 防止服务多次创建
        // 绑定服务
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    private void getData() {
        /*
        * 得到播放位置
         */
//        position = getIntent().getIntExtra("position",0);

        notification = getIntent().getBooleanExtra("notification",false);
        if (!notification){
            // 得到播放位置
            position = getIntent().getIntExtra("position",0);
        }
    }

    private void findViews() {
        setContentView(R.layout.activity_system_audio_player);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        seekbar_audio = (SeekBar) findViewById(R.id.seekbar_audio);
        bt_audio_playmode = (Button) findViewById(R.id.bt_audio_playmode);
        bt_audio_pre = (Button) findViewById(R.id.bt_audio_pre);
        bt_audio_start_pause = (Button) findViewById(R.id.bt_audio_start_pause);
        bt_audio_next = (Button) findViewById(R.id.bt_audio_next);
        bt_switch_lyric = (Button) findViewById(R.id.bt_switch_lyric);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        lyric_show_view = (LyricShowView) findViewById(R.id.lyric_show_view);

        iv_icon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) iv_icon.getBackground();
        drawable.start();

        bt_audio_playmode.setOnClickListener(this);
        bt_audio_pre.setOnClickListener(this);
        bt_audio_start_pause.setOnClickListener(this);
        bt_audio_next.setOnClickListener(this);
        bt_switch_lyric.setOnClickListener(this);

        // 设置seekbar 拖拽监听
        seekbar_audio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-08-08 21:48:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == bt_audio_playmode ) {
            // Handle clicks for btAudioPlaymode
            changePlaymode();
        } else if ( v == bt_audio_pre ) {
            // Handle clicks for btAudioPre
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == bt_audio_start_pause ) {
            // Handle clicks for btAudioStartPause
            try {
                if (service.isPlaying()){
                    // 暂停
                    service.pause();
                    // 按钮状态-设置播放
                    bt_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                } else {
                    // 播放
                    service.start();
                    bt_audio_start_pause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e){
                e.printStackTrace();
            }
        } else if ( v == bt_audio_next ) {
            // Handle clicks for btAudioNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == bt_switch_lyric ) {
            // Handle clicks for btSwitchLyric
        }
    }


    /**
     * 切换模式
     */
    private void changePlaymode() {
        try{
            // 得到模式
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL){
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL){
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            // 保存到服务器中
            service.setPlayMode(playmode);
            checkButtonStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void checkButtonStatus() {
        int playmode = 0;
        try {
            playmode = service.getPlayMode();

            if (playmode== MusicPlayerService.REPEAT_NORMAL){
                bt_audio_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
                bt_audio_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL){
                bt_audio_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                bt_audio_playmode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        if (receiver !=null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        if (conn!=null){
            unbindService(conn);
            conn = null;
        }
        handler.removeCallbacksAndMessages(null);

        // 2.取消注册
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC:
                    // 显示歌词
                    try{
                        int currentPosition = service.getCurrentPosition();
                        lyric_show_view.setNextShowLyric(currentPosition);
                    } catch (RemoteException e){
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
                case PROGRESS:
                    try {
                        int currentPostion = service.getCurrentPosition();
                        tv_time.setText(utils.stringForTime(currentPostion)+"/"+utils.stringForTime(service.getDuration()));
                        // SeekBar进度更新
                        seekbar_audio.setProgress(currentPostion);
                    } catch (RemoteException e){
                        e.printStackTrace();
                    }
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicPlayerService.OPEN_COMPLETE.equals(intent.getAction())){
                showViewData(null);
            }
        }
    }

    /**
     * 显示视图的数据
     */
//    private void showViewData() {
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showViewData(MediaItem mediaItem) {
        try {
            tv_artist.setText(service.getArtistName());
            tv_name.setText(service.getAudioName());

            // 得到总时长
            int duration = service.getDuration();
            seekbar_audio.setMax(duration);

            // 更新进度
            handler.sendEmptyMessage(PROGRESS);
            checkButtonStatus();

            String path = service.getAudioPath(); // mnt/sdcard/sudio/beijing.mp3
            path = path.substring(0,path.lastIndexOf("."));

            File file = new File(path+".lrc");
            if (!file.exists()){
                file = new File(path+".txt");
            }

            LyricParaser lyricParaser = new LyricParaser();
            try {
                // 解析歌词
                lyricParaser.readFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (lyricParaser.isExistsLyric()){
                lyric_show_view.setLyrics(lyricParaser.getLyricBeans());
                // 歌词同步
                handler.sendEmptyMessage(SHOW_LYRIC);
            }

        } catch (RemoteException e){
            e.printStackTrace();
        }
    }


}
