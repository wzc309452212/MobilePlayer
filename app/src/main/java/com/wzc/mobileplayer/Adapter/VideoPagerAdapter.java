package com.wzc.mobileplayer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzc.mobileplayer.R;

import java.util.ArrayList;

import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.Utils.Utils;

/**
 * Created by Administrator on 2017/5/23.
 * VideoPager的适配器
 */
public class VideoPagerAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MediaItem> mediaItems;
    /*
    * true:视频
    * false:音频
     */
    private boolean isVideo;
    private Utils utils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean b){
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
        this.isVideo = b;
    }


    @Override
    public int getCount() {
        // return 0;
        return mediaItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(context, R.layout.item_video_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            viewHolder.tv_size = (TextView) view.findViewById(R.id.tv_size);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(i);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_size.setText(android.text.format.Formatter.formatFileSize(context,mediaItem.getSize()));
        viewHolder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));

        if (!isVideo){
            // 音频
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return view;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }

}
