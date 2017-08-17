package com.wzc.mobileplayer.Pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wzc.mobileplayer.Adapter.VideoPagerAdapter;
import com.wzc.mobileplayer.Base.BasePager;
import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.R;
import com.wzc.mobileplayer.Utils.LogUtils;
import com.wzc.mobileplayer.activity.SystemAudioPlayer;
import com.wzc.mobileplayer.activity.SystemVideoPlayer;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/12.
 */
public class AudioPager extends BasePager {
//    TextView textView;
    private ListView listview;
    private TextView tv_no_media;
    private VideoPagerAdapter adapter;
    private ProgressBar progressBar;

    /*
    * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 设置适配器
            if (mediaItems!=null && mediaItems.size()>0){
                // 有数据 文本隐藏
                tv_no_media.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                adapter = new VideoPagerAdapter(context,mediaItems,false);
                // 设置适配器
                listview.setAdapter(adapter);
            } else {
                // 没有数据 文本显示
                tv_no_media.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }
        }
    };


    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtils.e("本地音乐页面被初始化了");
//        textView = new TextView(context);
//        textView.setTextSize(25);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);
        View view = View.inflate(context, R.layout.video_pager,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        // 设置item的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    public void initData(){
        super.initData();
        LogUtils.e("本地音乐数据被初始化了");
        // 在子线程中加载视频
        getDataFromLocal();
///        textView.setText("本地音乐页面");
    }

    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                // 初始化集合
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                //sdcard的音频路径
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME, // 在sdcard显示的音频名称
                        MediaStore.Audio.Media.DURATION, // 音频的时长
                        MediaStore.Audio.Media.SIZE, // 音频的大小
                        MediaStore.Audio.Media.DATA, // 在sdcard中的路径-播放地址
                        MediaStore.Audio.Media.ARTIST // 艺术家
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();

                        // 添加到集合
                        mediaItems.add(mediaItem);
                        mediaItem.setName(cursor.getString(0));
                        mediaItem.setDuration(cursor.getLong(1));
                        mediaItem.setSize(cursor.getLong(2));
                        mediaItem.setData(cursor.getString(3));
                        mediaItem.setArtist(cursor.getString(4));
                    }
                    cursor.close();
                }
                // 发消息-切换到主线程
                handler.sendEmptyMessage(2);

            }
        }.start();
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            // 传递列表数据
            Intent intent = new Intent(context, SystemAudioPlayer.class);

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            // 传递点击位置
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
