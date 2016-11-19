package com.mayday.xy.codingmusic.MainActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.exception.DbException;
import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.Mp3Info;
import com.mayday.xy.codingmusic.adapter.My_music_adapter;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/15.
 */

public class MyLikeMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView listView_like;
    private MyApplication app;
    private ArrayList<Mp3Info> list;
    private boolean isChang=false;

    @Override
    protected void onRestart() {
        super.onRestart();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindPlayService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_like_music_list);
        listView_like= (ListView) findViewById(R.id.listView_like);
        app=(MyApplication) getApplication();
        listView_like.setOnItemClickListener(this);
        initCollect();
    }

    private void initCollect() {
        try {
            //查询出数据库中所有的item
            list= (ArrayList<Mp3Info>) app.dbUtils.findAll(Mp3Info.class);
            listView_like.setAdapter(new My_music_adapter(this,list));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Progress(int progress) {}

    @Override
    public void Changes(int position) {}


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //得到当前的item进行播放
        if(!isChang){
            playServer.setMp3Infos(list);
        }
        playServer.play(i);
    }
}
