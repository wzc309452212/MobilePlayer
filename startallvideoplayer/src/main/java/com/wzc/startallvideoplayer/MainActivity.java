package com.wzc.startallvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAllVideoPlayers(View view){
        Intent intent = new Intent();
        // 第一参数：播放路径
        // 第二参数：路径对应的类型
        intent.setDataAndType(Uri.parse("http://localhost:8888/old%20drivers.mp4"),"video/*");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
