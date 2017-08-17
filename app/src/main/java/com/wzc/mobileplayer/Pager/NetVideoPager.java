package com.wzc.mobileplayer.Pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.wzc.mobileplayer.Adapter.NetVideoPagerAdapter;
import com.wzc.mobileplayer.Domain.MediaItem;
import com.wzc.mobileplayer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import com.wzc.mobileplayer.Base.BasePager;
import com.wzc.mobileplayer.Utils.Constant;
import com.wzc.mobileplayer.Utils.LogUtils;
import com.wzc.mobileplayer.activity.SystemVideoPlayer;

import java.util.ArrayList;

import io.vov.vitamio.utils.Log;

// import org.xutils.common.Callback;

/**
 * 作用：网络视频
 * Created by Administrator on 2017/5/12.
 */
public class NetVideoPager extends BasePager {
    // TextView textView;
    /*
    * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;
    private NetVideoPagerAdapter adapter;

    @ViewInject(R.id.listview)
    private ListView listview;

    @ViewInject(R.id.tv_no_media)
    private TextView tv_no_media;

    @ViewInject(R.id.refresh)
    MaterialRefreshLayout refreshLayout;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtils.e("网络视频页面被初始化了");
        View view = View.inflate(context,R.layout.net_video_pager,null);
        // 把view注入到xUtils3框中
        x.view().inject(NetVideoPager.this,view);

        // 设置列表监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        // 监听下拉和上拉列表
        refreshLayout.setMaterialRefreshListener(new MyMaterialRefreshListener());

        return view;
    }

    public void initData(){
        super.initData();
        LogUtils.e("网络视频页面被初始化了");
//      textView.setText("网络视频页面");
        getDataFromNet();
    }

    /**
     * 使用xutils3联网请求数据
     */
    private void getDataFromNet() {
        // 网络的路径
        RequestParams params = new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>(){

            @Override
            public void onSuccess(String result) {
                LogUtil.e("xUtils3联网请求成功=="+result);
                LogUtil.e("线程名称=="+Thread.currentThread().getName());
                processData(result);
                // 完成刷新
                if (!isLoadMore){
                    // 完成向下刷新
                    refreshLayout.finishRefresh();
                } else {
                    // 把上拉的隐藏
                    refreshLayout.finishRefreshLoadMore();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("xUtils3请求失败了=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+"xUtils3请求失败了=="+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");

            }
        });
    }

    /**
     * 解析json数据：gson解析、fastjson解析和手动解析（原生的api）
     * 显示数据-设置适配器
     * @param json
     */
    private void processData(String json) {
        if (!isLoadMore) {
            mediaItems = parsedJson(json);
            Log.e("TAG", "mediaItems.get(0).getName()==" + mediaItems.get(0).getName());

            if (mediaItems != null && mediaItems.size() > 0) {
                // 有数据
                tv_no_media.setVisibility(View.GONE);
                adapter = new NetVideoPagerAdapter(context, mediaItems);
                // 设置适配器
                listview.setAdapter(adapter);
            } else {
                tv_no_media.setVisibility(View.VISIBLE);
            }

        } else {
            // 加载更多
            ArrayList<MediaItem> mediaItem = parsedJson(json);
            mediaItem.addAll(mediaItem);
            // 刷新适配器
            adapter.notifyDataSetChanged(); // getCount->getView
        }


    }

    /**
     * 使用系统的接口解析json数据
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");

            for (int i = 0;i<jsonArray.length();i++){
                MediaItem mediaItem = new MediaItem();
                mediaItems.add(mediaItem); // 添加到集合中
                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String uri = jsonObjectItem.optString("url");
                mediaItem.setData(uri);
                String heightUri = jsonObjectItem.optString("highUrl");
                mediaItem.setHeightUri(heightUri);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUri(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 传递列表数据
            Intent intent = new Intent(context, SystemVideoPlayer.class);

            Bundle bundle = new Bundle();
            // 列表数据
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            // 传递点击的位置
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }

    /*
    *  是否加载更多
     */
    private boolean isLoadMore = false;

    private class MyMaterialRefreshListener extends MaterialRefreshListener {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            isLoadMore = false;
            getDataFromNet();
        }

        /**
         * 加载更多的回调
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            // Toast.makeText(context,"加载更多",Toast.LENGTH_SHORT).show();
            isLoadMore = true;
            getDataFromNet();
        }
    }
}
