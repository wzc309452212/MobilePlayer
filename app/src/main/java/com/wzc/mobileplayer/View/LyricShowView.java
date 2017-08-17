package com.wzc.mobileplayer.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wzc.mobileplayer.Domain.LyricBean;
import com.wzc.mobileplayer.Utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by admin on 2017/8/15.
 */

public class LyricShowView extends TextView {
    private int width;
    private int height;
    private ArrayList<LyricBean> lyricBean;
    private Paint paint;
    private Paint nopaint;
    private Context mContext;

    /**
     * 歌词的索引
     */
    private int index = 0;
    private float textHeight = 20;

    /**
     * 歌曲当前播放的进程
     */
    private int currentPostition;

    /**
     * 构造函数
     * @param context
     * @param attrs
     */

    public LyricShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        textHeight = DensityUtil.dip2px(mContext,20);
        initView();
    }

    private void initView() {
        // 创建画笔
        paint = new Paint();
        paint.setTextSize(DensityUtil.dip2px(mContext,16));
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        nopaint = new Paint();
        nopaint.setTextSize(DensityUtil.dip2px(mContext,16));
        nopaint.setColor(Color.WHITE);
        nopaint.setTextAlign(Paint.Align.CENTER);
        nopaint.setAntiAlias(true);

//        lyricBean = new ArrayList<>();
//        // 添加歌词列表
//        LyricBean lyricBean1 = new LyricBean();
//        for (int i = 0; i<1000; i++){
//            // 歌词内容
//            lyricBean1.setContent("aaaaa"+i);
//            // 休眠时间
//            lyricBean1.setSleeptime(i+1000);
//            // 时间戳
//            lyricBean1.setTimePoint(i*1000);
//            // 添加到集合中
//            lyricBean.add(lyricBean1);
//            // 重新创建
//            lyricBean1 = new LyricBean();
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    
    

    /**
     * 绘制歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyricBean != null && lyricBean.size()>0){
            // 绘制歌词
            // 当前句-绿色
            String content = lyricBean.get(index).getContent();
            canvas.drawText(content,width/2,height/2,paint);
            // 绘制前面部分
            float tempY = height/2;
            for (int i = index -1; i>=0; i--){
                tempY = tempY - textHeight;
                if (tempY<0){
                    break;
                }
                String preContent = lyricBean.get(i).getContent();
                canvas.drawText(preContent,width/2,tempY,nopaint);
            }

            // 绘制后面部分
            tempY = height/2;
            for (int i = index+1;i<lyricBean.size();i++){
                tempY = tempY +textHeight;
                if (tempY >height){
                    break;
                }
                String nextContent = lyricBean.get(i).getContent();
                canvas.drawText(nextContent,width/2,tempY,nopaint);
            }
        } else {
            // 没有歌词
            canvas.drawText("没有找到歌词...",width/2,height/2,paint);
        }
    }

    /**
     * 根据当前播放的位置 计算高亮哪句 并且与歌曲播放同步
     * @param currentPosition
     */
    public void setNextShowLyric(int currentPosition) {
        this.currentPostition = currentPosition;
        if (lyricBean == null || lyricBean.size() == 0){
            return;
        }
        for (int i = 1; i<lyricBean.size();i++){
            if (currentPosition<lyricBean.get(i).getTimePoint()){
                int indexTemp = i -1;
                if (currentPosition<=lyricBean.get(indexTemp).getTimePoint()){
                    // 就是我们要找的高亮的那句
                    index = indexTemp;
                }
            }
        }
        invalidate(); // 强制绘制
    }

    /**
     * 设置歌词列表
     * @param lyricBeans
     */
    public void setLyrics(ArrayList<LyricBean> lyricBeans) {
        this.lyricBean = lyricBeans;
    }
}
