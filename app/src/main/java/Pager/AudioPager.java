package Pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import Base.BasePager;
import Utils.LogUtils;

/**
 * Created by Administrator on 2017/5/12.
 */
public class AudioPager extends BasePager {
    TextView textView;

    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtils.e("本地音乐页面被初始化了");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);

        return textView;
    }

    public void initData(){
        super.initData();
        LogUtils.e("本地音乐页面被初始化了");
        textView.setText("本地音乐页面");
    }
}
