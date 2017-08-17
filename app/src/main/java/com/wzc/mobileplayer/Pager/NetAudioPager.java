package com.wzc.mobileplayer.Pager;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wzc.mobileplayer.Adapter.VideoPagerAdapter;
import com.wzc.mobileplayer.Base.BasePager;
import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.Utils.LogUtils;

import java.util.ArrayList;


import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2017/5/12.
 */
public class NetAudioPager extends BasePager {
    TextView textView;
    private ListView listview;
    private TextView tv_no_media;
    private VideoPagerAdapter adapter;

    /*
    * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 设置适配器
            if (mediaItems !=null && mediaItems.size()>0){
                // 有数据 文本隐藏
                tv_no_media.setVisibility(View.GONE);
                adapter = new VideoPagerAdapter(context,mediaItems,false);
                // 设置适配器
                listview.setAdapter(adapter);
            } else {
                // 没有数据 文本显示
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtils.e("网络音乐页面被初始化了");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);

        return textView;


    }

    public void initData(){
        super.initData();
        LogUtils.e("网络音乐页面被初始化了");
        textView.setText("网络音乐页面");
    }
}
