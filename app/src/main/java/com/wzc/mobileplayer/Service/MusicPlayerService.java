package com.wzc.mobileplayer.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.IMusicPlayerService;
import com.wzc.mobileplayer.R;
import com.wzc.mobileplayer.Utils.CacheUtils;
import com.wzc.mobileplayer.activity.SystemAudioPlayer;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.Event;

import java.io.IOException;
import java.util.ArrayList;

import io.vov.vitamio.utils.Log;

public class MusicPlayerService extends Service {
    public static final String OPEN_COMPLETE = "open_complete";

    // AIDL 生成的类
    IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        // 把服务当成成员变量
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            service.setPlayMode(mode);
        }

        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        public void seekTo(int i) throws RemoteException {
            service.seekTo(i);
        }

        public String getAudioPath() throws RemoteException {
            return mediaItem.getData();
        }
    };

    /**根据位置打开一个音频并且播放
     * @param position
     */
    private void openAudio(int position) {
        if (mediaItems!=null && mediaItems.size()>0){
            mediaItem = mediaItems.get(position);
            this.position = position;

            // MediaPlayer
            if (mediaPlayer != null){
                mediaPlayer.reset(); // 上一曲重置
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            // 设置三个监听
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
            mediaPlayer.setOnErrorListener(new MyOnErrorListener());

            // 设置播放地址
            try {
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();
                isNext = false;
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if (!isLoaded){
            Toast.makeText(this,"没有加载完成",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    *  音频是否加载完成
     */
    private boolean isLoaded = false;
    private ArrayList<MediaItem> mediaItems;
    private MediaItem mediaItem;
    private int position;
    private MediaPlayer mediaPlayer;

    /**
     * 返回代理类
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
        // TODO: Return the communication channel to the service.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","service=="+this.toString());
        playmode = CacheUtils.getPlaymode(this,"playmode");
        getDataFromLocal();
    }

    /**
     * 在子线程中得到音频
     */
    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();

                // 初始化集合
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                // sdcard的视频路径
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME, // 在sdcard显示的视频名称
                        MediaStore.Audio.Media.DURATION, // 视频的时长,毫秒
                        MediaStore.Audio.Media.SIZE, // 文件大小-byte
                        MediaStore.Audio.Media.DATA, // 在sdcard中的路径-播放地址
                        MediaStore.Audio.Media.ARTIST // 艺术家
                };

                Cursor cursor = resolver.query(uri,objs,null,null,null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();

                        // 添加到集合中
                        mediaItems.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3); // 播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4); // 演唱者
                        mediaItem.setArtist(artist);

                    }
                    cursor.close();
                }
                // 音频加载完成
                isLoaded = true;
            }
        }.start();
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            // notifyChange(OPEN_COMPLETE);
            EventBus.getDefault().post(mediaItem);
            start();
        }
    }

    /**
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        // 发广播
        sendBroadcast(intent);
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            isNext = true;
            next();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    /**
     * 开始播放音频
     */
    private void start() {

        mediaPlayer.start();
        // 在状态栏创建通知
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, SystemAudioPlayer.class);
        intent.putExtra("notification",true); // 标识来自状态栏
        // 包含意图
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            notification = new Notification.Builder(this)
                    // 图片
                    .setSmallIcon(R.drawable.notification_music_playing)
                    // 标题
                    .setContentTitle("沧艺悦听")
                    // 内容
                    .setContentText("正在播放："+getAudioName())
                    // 点击动作 延期意图
                    .setContentIntent(pendingIntent)
                    .build();
            // 点击后还存在属性
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        }
        nm.notify(1,notification);
    }

    /**
     * 播放下一首歌曲
     */
    private void next() {
        // 设置下一曲对应的位置
        setNextPosition();
        // 根据对应的位置去播放
        openNextAudio();
    }

    private void openNextAudio() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL){
            if (position <= mediaItems.size()-1){
                openAudio(position);
            } else {
                position = mediaItems.size()-1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        } else {
            if (position<= mediaItems.size() -1){
                openAudio(position);
            } else {
                position = mediaItems.size() -1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL){
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
            if (!isNext){
                isNext = false;
                position++;
                if (position>mediaItems.size()-1){
                    position=0;
                }
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL){
            position++;
            if (position>mediaItems.size() -1 ){
                position =0;
            }
        } else {
            position++;
        }
    }

    /*
    * 暂停
     */
    private void pause(){
        mediaPlayer.pause();
        // 移除状态栏的通知
        nm.cancel(1);
    }

    /**得到歌曲的名称
     * @return
     */
    private String getAudioName(){
        if (mediaItem!=null){
            return mediaItem.getName();
        }
        return "";
    }

    /**得到演唱者的名字
     * @return
     */
    private String getArtistName(){

        if (mediaItem !=null){
            return mediaItem.getArtist();
        }
        return "";
    }

    /**得到歌曲的当前播放进度
     * @return
     */
    private int getCurrentPosition(){

        if (mediaItem!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    private int getDuration(){

        if (mediaItem!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 播放上一曲
     */
    private void pre(){
        // 设置下一曲对应的位置
        setPrePosition();
        // 根据对应的位置去播放
        openPreAudio();

    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL){
            if (position>=0){
                openAudio(position);
            } else {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        } else {
            if (position>=0){
                openAudio(position);
            } else {
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL){
            position--;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE){
            if (!isNext){
                isNext = false;
                position--;
                if (position<0){
                    position = mediaItems.size()-1;
                }
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL){
            position--;
            if (position<0){
                position = mediaItems.size()-1;
            }
        } else {
            position--;
        }
    }

    /**得到播放模式
     * @return
     */
    private int getPlayMode(){
        return playmode;
    }

    /**设置播放模式
     * @param mode
     */
    private void setPlayMode(int mode){
        this.playmode = mode;
        CacheUtils.setPlaymode(this,"playmode",playmode);
    }

    public boolean isPlaying() throws RemoteException {
        return mediaPlayer.isPlaying();
    }

    public void seekTo(int position) throws RemoteException{
        mediaPlayer.seekTo(position);
    }

    private NotificationManager nm;

    // 顺序播放
    public static final int REPEAT_NORMAL = 1;
    // 单曲循环
    public static final int REPEAT_SINGLE = 2;
    // 全部循环
    public static final int REPEAT_ALL = 3;
    private int playmode = REPEAT_NORMAL;

    private boolean isNext = false;





}
