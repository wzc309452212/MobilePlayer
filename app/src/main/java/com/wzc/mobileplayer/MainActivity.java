package com.wzc.mobileplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Base.BasePager;
import Base.ReplaceFragment;
import Pager.AudioPager;
import Pager.NetAudioPager;
import Pager.NetVideoPager;
import Pager.VideoPager;


/**
 * Created by Administrator on 2017/5/9.
 */
public class MainActivity extends FragmentActivity{

    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;
    /*
    页面的集合
     */
    private ArrayList<BasePager> basePagers;
    /*
    选中的位置
     */
    private int position;
    /*
    *缓存的basePager
     */
    private BasePager tempBasePager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView textView = new TextView(this);
//        textView.setText("我是主页面");
//        textView.setTextSize(30);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);

        // Log.e("TAG","onCreate");

          isGrantExternalRW(this);

        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);

        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);

        initBasepager();



        // 设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        /*
        默认选中本地视频
         */
        rg_bottom_tag.check(R.id.rb_video);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }

           // BasePager-当前的basePager
            BasePager currentPager = basePagers.get(position);
            setFragment(currentPager);

        }
    }

    /*
     * 初始化BasePager
     * 有先后顺序要求
     */
    private void initBasepager() {
        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this)); // 添加本地视频页面 0
        basePagers.add(new AudioPager(this)); // 添加本地音乐页面 1
        basePagers.add(new NetVideoPager(this)); // 添加网络视频页面 2
        basePagers.add(new NetAudioPager(this)); // 添加网络音乐页面 3
    }

    private void setFragment(BasePager currentPager) {
        if (tempBasePager != currentPager) {

            // 1.得到FragmentManager
            FragmentManager manager = getSupportFragmentManager();
            // 2.开启事务
            FragmentTransaction ft = manager.beginTransaction();
            // 3.替换
       /*
        ft.replace(R.id.fl_main_content,new Fragment(){
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                BasePager basepager = getBasePager();
                if (basepager!=null) {
                    // 各个页面的视图
                    return basepager.rootView;
                }
                return null;
            }
        });
        */
            // ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
            if (currentPager != null) {
                // 是否添加过
                if (!currentPager.isAdded()) {
                    // 把之前显示的隐藏
                    if (tempBasePager != null) {
                        ft.hide(tempBasePager);
                    }
                    // 如果没有添加就添加
                    ft.add(R.id.fl_main_content, currentPager);
                } else {
                    // 把之前的隐藏
                    if (tempBasePager != null) {
                        ft.hide(tempBasePager);
                    }
                    // 如果添加了就直接显示
                    ft.show(currentPager);
                }
                // 4.提交事务
                ft.commit();
            }
            tempBasePager = currentPager;
        }

    }

    /*
    根据位置得到相应的页面
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager!=null &&!basePager.isInitData ){
            basePager.initData(); // 联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG","onDestroy");
    }

    /**
     +     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     +     * @param activity
     +     * @return
     +     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
