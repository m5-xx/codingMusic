package com.mayday.xy.codingmusic.MainActivitys;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayServer extends Service {

    private ArrayList<Mp3Info> mp3Infos;
    private MediaPlayer mPlay;
    private int currentPosition;
    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es= Executors.newSingleThreadExecutor();
    private boolean isPause=false;

    @Override
    public void onCreate() {
        mPlay = new MediaPlayer();
        mp3Infos = MediaUtils.getmp3Infos(this);
        //启动的音乐的时候去调用进度，因为已经进行判断了
        es.execute(runnable);
        super.onCreate();
    }

    //Service与Activity通信
    class PlayBind extends Binder {
        public PlayServer getPlayService() {
            return PlayServer.this;
        }
    }

    public PlayServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBind();
    }

    @Override
    public void onDestroy() {
        //回收一下线程
        if (es != null && es.isShutdown()==false) {
            es.shutdown();
            es = null;
        }
        super.onDestroy();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (musicUpdateListener != null && mPlay != null && mPlay.isPlaying()) {
                while (true) {
                    musicUpdateListener.onProgress(getCurrentProgress());
                    Log.d("MAYDAY", "123456789987654321");
                }
            }
            //每隔0.5s更新一下进度条
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    //从头播放
    public void play(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            try {
                mPlay.reset();
                mPlay.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlay.prepare();
                mPlay.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //自动定位到所播放歌曲的位置
            if (musicUpdateListener != null) {
                musicUpdateListener.onChanges(currentPosition);
            }
        }
    }

    public boolean isPlaying() {
        return mPlay.isPlaying();
    }

    public boolean isPause(){
        return isPause;
    }



    //暂停
    public void pause() {
        if (mPlay.isPlaying()) {
            mPlay.pause();
            isPause=true;
        }

    }

    //上一首
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);

    }

    public void next() {
        if (currentPosition + 1 >= mp3Infos.size()) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);
    }

    //暂停播放
    public void start() {
        if (mPlay != null && !mPlay.isPlaying()) {
            mPlay.start();
        }
    }

    public int getDuration() {
        return mPlay.getDuration();
    }

    //跳到某一首
    public void seekTo(int msec) {
        mPlay.seekTo(msec);
    }

    //得到当前进度
    public int getCurrentProgress() {
        if (mPlay != null && mPlay.isPlaying()) {
            return mPlay.getCurrentPosition();
        }
        return 0;
    }

    public int getCurrentPositions() {
        return currentPosition;
    }


    //获取Service与Activity通讯时线程的情况(回调接口)
    public interface MusicUpdateListener {
        //获取当前的进度
        void onProgress(int progress);

        //获取信息
        void onChanges(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
