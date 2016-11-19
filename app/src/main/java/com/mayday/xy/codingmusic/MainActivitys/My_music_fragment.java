package com.mayday.xy.codingmusic.MainActivitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;
import com.mayday.xy.codingmusic.adapter.My_music_adapter;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/3.
 */
public class My_music_fragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private ImageView iv_album; //专辑封面图
    private TextView songName, singer;  //歌曲名称,歌手
    private ImageView lv_play, lv_next;//播放按钮,下一首

    //判断是否为暂停状态
//    private boolean isPause=false;

    static My_music_fragment music_fragment;
    private MainActivity mainActivity;
    private ArrayList<Mp3Info> mp3Info;
    private My_music_adapter adapter;


//    private int position;

    //提供方法供外界调用
    public static My_music_fragment newInstance() {
        music_fragment = new My_music_fragment();
        return music_fragment;
    }

    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout, null);
        listView = (ListView) view.findViewById(R.id.lv_listview);

        iv_album = (ImageView) view.findViewById(R.id.iv_album);
        songName = (TextView) view.findViewById(R.id.songName);
        songName.getPaint().setFakeBoldText(true);
        singer = (TextView) view.findViewById(R.id.singer);
        lv_play = (ImageView) view.findViewById(R.id.lv_play);
        lv_next = (ImageView) view.findViewById(R.id.lv_next);

        listView.setOnItemClickListener(this);
        lv_play.setOnClickListener(this);
        lv_next.setOnClickListener(this);
        iv_album.setOnClickListener(this);
        loadDate();
        return view;
    }

    //当切换回主界面的时候还会从新调用该方法，因此就可以达到一个同步的更新UI了
    @Override
    public void onResume() {
        mainActivity.bindPlayService();
        super.onResume();
    }

    @Override
    public void onPause() {
        mainActivity.unBindPlayService();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadDate() {
        mp3Info = MediaUtils.getmp3Infos(mainActivity);
        adapter = new My_music_adapter(mainActivity, mp3Info);
        adapter.notifyDataSetChanged();     //加入一个临时的缓冲区
        listView.setSelection(0);
        listView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        mainActivity.playServer.play(i);
        lv_play.setImageResource(R.mipmap.play);
    }


    //更新列表框下的播放UI
    public void changUIStatusOnPlay(int position) {
        if (position >= 0 && position < mp3Info.size()) {
            Mp3Info mp3Info = this.mp3Info.get(position);
            songName.setText(mp3Info.getTitle());
            singer.setText(mp3Info.getArtist());
//            lv_play.setImageResource(R.mipmap.pause);
            //显示专辑封面
            Bitmap bitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true);
            iv_album.setImageBitmap(bitmap);
//            this.position = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_play: {
                if (mainActivity.playServer.isPlaying()) {
                    lv_play.setImageResource(R.mipmap.pause);
                    mainActivity.playServer.pause();
                } else {
                    if (mainActivity.playServer.isPause()) {
                        lv_play.setImageResource(R.mipmap.play);
                        mainActivity.playServer.start();
                    } else {
                        //如果不是暂停后播放就从第一首开始播放,后面会通过SP来处理
                        mainActivity.playServer.play(mainActivity.playServer.getCurrentPositions());
                        lv_play.setImageResource(R.mipmap.play);
                    }
                }
                break;
            }
            case R.id.lv_next:
                mainActivity.playServer.next();
                lv_play.setImageResource(R.mipmap.play);
                break;

            case R.id.iv_album:
                Intent intent = new Intent(mainActivity, PlayActivity.class);
//                intent.putExtra("position",position);
                startActivityForResult(intent,1);
//                startActivity(intent);
                break;
        }


    }
}
