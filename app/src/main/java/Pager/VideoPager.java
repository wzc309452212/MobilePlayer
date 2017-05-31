package Pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wzc.mobileplayer.R;
import com.wzc.mobileplayer.SystemVideoPlayer;

import java.util.ArrayList;

import Adapter.VideoPagerAdapter;
import Base.BasePager;
import Domain.MediaItem;
import Utils.LogUtils;

/**
 * Created by Administrator on 2017/5/12.
 */
public class VideoPager extends BasePager {
    // TextView textView;
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;

    // 设置适配器
    private VideoPagerAdapter videoPagerAdapter;


    // 装数据的集合
    private ArrayList<MediaItem> mediaItems;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size()>0){
                // 有数据
                // 设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context,mediaItems);
                listview.setAdapter(videoPagerAdapter);
                // 把文本隐藏
                tv_nomedia.setVisibility(View.GONE);

            } else {
                // 没有数据
                // 文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }
            // ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);

        }
    };

    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtils.e("本地视频页面被初始化了");
        /*textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);*/
        View view = View.inflate(context, R.layout.video_pager,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        // 设置ListView的Item的点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());

        // return textView;
        return view;
    }

    public void initData(){
        super.initData();
        LogUtils.e("本地视频页面被初始化了");
        // textView.setText("本地视频页面");

        //加载本地视频数据
        getDataFromLocal();
    }

//    private void getDataFromLocal() {
//        new Thread(){
//
//        }.start();
//
//    }
    /*
    从本地的sdcard得到数据
    1.遍历sdcard，后缀名
    2.从内容提供者里面获取视频
    3.如果是6.0以上系统,需要获取动态
    获取读取sdcard的权限
    */

    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME, // 视频文件在sdcard中的名称
                        MediaStore.Video.Media.DURATION, // 视频总时长
                        MediaStore.Video.Media.SIZE, // 视频文件的大小
                        MediaStore.Video.Media.DATA, // 视频的绝对地址
                        MediaStore.Video.Media.ARTIST //歌曲的演唱者
                };

                Cursor cursor = resolver.query(uri,objs,null,null,null);

                if (cursor != null) {
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);  // 写在上面或下面都可以

                        String name = cursor.getString(0); // 视频的名称
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1); // 视频的时长
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2); // 视频的大小
                        mediaItem.setSize(size);
                        String data = cursor.getString(3); // 视频的播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4); // 艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                // handler发消息 （在Cursor的外边,不管Cursor是否为空)
                handler.sendEmptyMessage(10);

            }
        }.start();

    }



    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            Toast.makeText(context, "mediaItem == "+mediaItem.toString(), Toast.LENGTH_SHORT).show();

            // 1、调起系统所有的播放-隐式意图
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            // 2.调用自己写的播放器-显式意图
             Intent intent = new Intent(context,SystemVideoPlayer.class);
             intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
             context.startActivity(intent);

        }
    }
}
