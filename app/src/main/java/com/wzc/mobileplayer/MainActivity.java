package com.wzc.mobileplayer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView textView = new TextView(this);
//        textView.setText("我是主页面");
//        textView.setTextSize(30);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);

        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this)); // 添加本地视频页面 0
        basePagers.add(new AudioPager(this)); // 添加本地音乐页面 1
        basePagers.add(new NetVideoPager(this)); // 添加网络视频页面 2
        basePagers.add(new NetAudioPager(this)); // 添加网络音乐页面 3

        // 设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        /*
        默认选中首页
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
            setFragment();

        }
    }

    private void setFragment() {
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
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        // 4.提交事务
        ft.commit();
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

}
