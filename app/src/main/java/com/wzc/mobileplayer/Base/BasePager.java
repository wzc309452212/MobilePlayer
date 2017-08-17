package com.wzc.mobileplayer.Base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/5/12.
 */
public abstract class BasePager extends Fragment {
    /*
    上下文
     */
    public Context context;

    /*
    *当系统创建当前BaseFragment类的时候回调
    * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    /*
    *当系统要创建Fragment的视图的时候回调这个方法
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    public View rootView;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /*
    强制由孩子实现，实现特定效果
     */
    public abstract View initView();
    /*
    *Activity创建成功的时候回调该方法
    * 初始化数据
    * 联网请求数据
    * 绑定数据
    * @param savedInstanceState
     */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /*当子页面需要初始化数据，
        *1、联网请求数据或者绑定数据的时候要重写该方法
        *2、绑定数据
        */
    public void initData(){

    }
}
