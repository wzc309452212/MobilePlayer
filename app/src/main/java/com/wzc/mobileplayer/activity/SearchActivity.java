package com.wzc.mobileplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wzc.mobileplayer.R;

/**
 * Created by admin on 2017/8/21.
 */

public class SearchActivity extends Activity implements View.OnClickListener{

    private EditText etSearch;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressBar;
    private TextView tvNodata;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-08-21 21:36:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_search);
        etSearch = (EditText)findViewById( R.id.et_search );
        ivVoice = (ImageView)findViewById( R.id.iv_voice );
        tvSearch = (TextView)findViewById( R.id.tv_search );
        listview = (ListView)findViewById( R.id.listview );
        progressBar = (ProgressBar)findViewById( R.id.progressBar );
        tvNodata = (TextView)findViewById( R.id.tv_nodata );

        ivVoice.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_voice:
                Toast.makeText(this, "语音输入", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_search:
                Toast.makeText(this,"搜索",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
