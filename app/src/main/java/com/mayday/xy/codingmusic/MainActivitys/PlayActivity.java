package com.mayday.xy.codingmusic.MainActivitys;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;

import java.util.ArrayList;


/**
 * Created by xy-pc on 2016/11/6.
 */
public class PlayActivity extends BaseActivity implements View.OnClickListener {

    private static final int UPDATA_TIME = 0x1;
    private TextView songName, start_time, end_time;
    private ImageView iv_musicAlumb;
    private SeekBar seekBar;
    private ImageButton way, back, play, next;

    private ArrayList<Mp3Info> mp3Infos;
    private Mp3Info mp3Info;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);

        songName = (TextView) findViewById(R.id.songName);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        iv_musicAlumb = (ImageView) findViewById(R.id.iv_musicAlbum);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        way = (ImageButton) findViewById(R.id.way);

        back = (ImageButton) findViewById(R.id.back);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);

        mp3Infos = MediaUtils.getmp3Infos(this);
        bindPlayService();
        myHandler = new MyHandler(this);

        play.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);

    }

    static class MyHandler extends Handler {
        protected PlayActivity playActivity;

        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATA_TIME:
//                        start_time.setText(MediaUtils.formatTime());
                        //更新进度时间
                        playActivity.start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    }


    @Override
    public void Progress(int progress) {
        Message message = myHandler.obtainMessage(UPDATA_TIME);
        message.arg1 = progress;
        myHandler.sendMessage(message);

//        start_time.setText(MediaUtils.formatTime(progress));      子线程不能直接去更新UI，如要给其一个Handler
        seekBar.setProgress(progress);
    }

    @Override
    public void Changes(final int position) {
        //先判断是否处于播放的状态(需要进行异步处理)
        if (this.playServer.isPlaying()) {
            play.setImageResource(R.mipmap.play);

            mp3Info =mp3Infos.get(position);
            songName.setText(mp3Info.getTitle());
            Bitmap bitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true);
            iv_musicAlumb.setImageBitmap(bitmap);

            end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
            seekBar.setMax((int) mp3Info.getDuration());
        }
    }



    @Override
    protected void onDestroy() {
        unBindPlayService();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (playServer.isPlaying()) {
                    play.setImageResource(R.mipmap.pause);
                    playServer.pause();
                } else {
                    if (playServer.isPause()) {
                        play.setImageResource(R.mipmap.play);
                        playServer.start();
                    } else {
                        //如果不是暂停后播放就从第一首开始播放,后面会通过SP来处理
                        playServer.play(0);
                        play.setImageResource(R.mipmap.play);
                    }
                }
                break;
            case R.id.back:
                playServer.prev();
                play.setImageResource(R.mipmap.play);
                break;
            case R.id.next:
                playServer.next();
                play.setImageResource(R.mipmap.play);
                break;
            default:
                break;
        }

    }
}
