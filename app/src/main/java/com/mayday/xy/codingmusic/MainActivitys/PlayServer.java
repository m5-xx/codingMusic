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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayServer extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private ArrayList<Mp3Info> mp3Infos;
    private MediaPlayer mPlay;
    private int currentPosition;
    private MusicUpdateListener musicUpdateListener;
    private Mp3Info mp3Info;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isPause = false;

    public static final int LOOP_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int ONES_PLAY = 3;

    //选择播放模式,默认是循环播放
    public int play_mode = LOOP_PLAY;


    public int getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public void onCreate() {
        //将保存的状态还原，这里需要修改AndroidManifest.xml,因为我们自定义了Application
        MyApplication app= (MyApplication)getApplication();
        currentPosition = app.sp.getInt("currentPosition", 0);
        play_mode = app.sp.getInt("play_mode", PlayServer.LOOP_PLAY);

        mPlay = new MediaPlayer();
        mp3Infos = MediaUtils.getmp3Infos(this);
        mPlay.setOnCompletionListener(this);
        mPlay.setOnErrorListener(this);
    }

    //该方法用于判断播放模式
    Random random = new Random();
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (play_mode) {
            case LOOP_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size()));
                break;
            case ONES_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }


    //Service与Activity通信
    class PlayBind extends Binder {
        public PlayServer getPlayService() {
            return PlayServer.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBind();
    }

    @Override
    public void onDestroy() {
        //回收一下线程
        if (es != null && es.isShutdown() == false) {
            es.shutdown();
            es = null;
        }
        super.onDestroy();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("MAYDAY1", "123456789987654321-----------------------------");
            if (musicUpdateListener != null && mPlay != null && mPlay.isPlaying()) {
                Log.d("MAYDAY2", "123456789987654321-----------------");
                while (true) {
                    musicUpdateListener.onProgress(getCurrentProgress());
                }
            }
            //每隔1s更新一下进度条
            try {
                Thread.sleep(1000);
                Log.d("MAYDAY3", "123456789987654321-----------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    //从头播放
    public void play(int position) {
        if (position < 0 && position >= mp3Infos.size()) {
            mp3Info = mp3Infos.get(0);
        }
            mp3Info = mp3Infos.get(position);
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
                //开启播放线程
                es.execute(runnable);
            }
    }

    public boolean isPlaying() {
        return mPlay.isPlaying();
    }

    public boolean isPause() {
        return isPause;
    }


    //暂停
    public void pause() {
        if (mPlay.isPlaying()) {
            mPlay.pause();
            isPause = true;
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

    //在这里就不用获取总的时间长度了
    public int getDuration() {
        return mPlay.getDuration();
    }

    //跳到某处
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

    //提供一个set方法供基Activity来绑定
    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
