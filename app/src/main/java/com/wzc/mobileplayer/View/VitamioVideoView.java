package com.wzc.mobileplayer.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by admin on 2017/7/27.
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView{

    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 根据传入视频的大小 设置视频的画面大小
     * @param screenWidth
     * @param screenHeight
     */
    public void setViewSize(int screenWidth,int screenHeight){
// 视频画面的宽和高
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = screenWidth;
        l.height = screenHeight;
        setLayoutParams(l);
    }
}
