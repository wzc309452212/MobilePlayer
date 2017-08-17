package com.wzc.mobileplayer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.R;
import com.wzc.mobileplayer.Utils.Utils;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/23.
 * VideoPager的适配器
 */
public class NetVideoPagerAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MediaItem> mediaItems;
    private Utils utils;
    private ImageOptions imageOptions;

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120),DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content，不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它
                // 加载中或错误图片的ScaleType
                // .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default) // 加载过程中的默认图片
                .setFailureDrawableId(R.drawable.video_default) // 挨着出错的图片
                .build();
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
            view = View.inflate(context,R.layout.item_net_video_pager,null);
            // view = View.inflate(context, R.layout.item_net_video_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            viewHolder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
//            viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            viewHolder.tv_size = (TextView) view.findViewById(R.id.tv_size);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(i);
        viewHolder.tv_name.setText(mediaItem.getName());
        viewHolder.tv_desc.setText(mediaItem.getDesc());
        // 我的错误尝试viewHolder.tv_size.setText((int) mediaItem.getSize());
        viewHolder.tv_size.setText(android.text.format.Formatter.formatFileSize(context,mediaItem.getSize()));
       // viewHolder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        //我的错误尝试 viewHolder.tv_duration.setText((String) mediaItem.getDuration());
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));

        // 请求图片
        x.image().bind(viewHolder.iv_icon,mediaItem.getImageUri(),imageOptions);

        return view;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
//        TextView tv_time;
        TextView tv_size;
        TextView tv_duration;
        TextView tv_desc;
    }

}
