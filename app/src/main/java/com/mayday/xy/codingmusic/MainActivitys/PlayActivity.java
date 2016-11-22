package com.mayday.xy.codingmusic.MainActivitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;
import com.mayday.xy.codingmusic.adapter.MyViewPagerAdapter;

import java.util.ArrayList;

import static com.mayday.xy.codingmusic.MainActivitys.PlayServer.LOOP_PLAY;
import static com.mayday.xy.codingmusic.MainActivitys.PlayServer.ONES_PLAY;
import static com.mayday.xy.codingmusic.MainActivitys.PlayServer.RANDOM_PLAY;


/**
 * Created by xy-pc on 2016/11/6.
 */
public class PlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int UPDATA_TIME = 0x1;
    private TextView songName, start_time, end_time;
    private ImageView iv_musicAlumb;
    private SeekBar seekBar;
    private ImageButton way, back, play, next, isLike;
    private ViewPager viewPager;
    private ArrayList<View> viewList;
    private ArrayList<Mp3Info> mp3Infos;
    private Mp3Info mp3Info;
    private MyApplication app;
//    private Mp3Info likeMp3info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        app = (MyApplication) getApplication();

        viewPager = (ViewPager) findViewById(R.id.viewPagerId);
        viewList = new ArrayList<>();
        initView();
        viewPager.setAdapter(new MyViewPagerAdapter(viewList));


//        songName = (TextView) findViewById(R.id.songNames);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
//        iv_musicAlumb = (ImageView) findViewById(R.id.iv_musicAlbum);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        way = (ImageButton) findViewById(R.id.way);
        isLike = (ImageButton) findViewById(R.id.isLike);
        back = (ImageButton) findViewById(R.id.back);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);

        mp3Infos = MediaUtils.getmp3Infos(this);
        bindPlayService();
        myHandler = new MyHandler(this);

        way.setOnClickListener(this);
        play.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        isLike.setOnClickListener(this);


    }

    //初始化VIEWPAGER
    private void initView() {
        //加载专辑界面
        View view = getLayoutInflater().inflate(R.layout.music_information, null);
        songName = (TextView) view.findViewById(R.id.songNames);
        iv_musicAlumb = (ImageView) view.findViewById(R.id.iv_musicAlbum);
        viewList.add(view);
        //加载歌词界面
        viewList.add(getLayoutInflater().inflate(R.layout.lyric_layout, null));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (b) {
            playServer.pause();
            playServer.seekTo(progress);
            playServer.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //开始拖拽的位置
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //结束拖拽的位置
    }

    private static MyHandler myHandler;

    static class MyHandler extends Handler {
        protected PlayActivity playActivity;

        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATA_TIME:
                        //更新进度时间
                        playActivity.start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
            super.handleMessage(msg);
        }
        /*@Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATA_TIME:
                        //更新进度时间
                        playActivity.start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }*/
    }

    /**
     * 用于计时以及显示播放进度的函数
     *
     * @param progress
     */

    @Override
    public void Progress(int progress) {
        //无法获取到progressd的值
        Message message = myHandler.obtainMessage(UPDATA_TIME);
        message.arg1 = progress;
        myHandler.sendMessage(message);
//        start_time.setText(MediaUtils.formatTime(progress));      子线程不能直接去更新UI，要给其一个Handler
        seekBar.setProgress(progress);
    }

    @Override
    public void Changes(final int position) {
        //先判断是否处于播放的状态(需要进行异步处理)
        mp3Info = mp3Infos.get(position);
        songName.setText(mp3Info.getTitle());
        Bitmap bitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true);
        iv_musicAlumb.setImageBitmap(bitmap);

        end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        seekBar.setMax((int) mp3Info.getDuration());
        if (playServer.isPlaying()) {
            play.setImageResource(R.mipmap.pause);
        } else {
            play.setImageResource(R.mipmap.play);
        }
        //当我们回到my_music_fragment然后在回来的时候就会去判断此时的一个播放状态及显示对应的图片
        switch (playServer.getPlay_mode()) {
            //可以说该switch就只是一个辅助吧!(为了防止getTag()报空指针0.0)
            case LOOP_PLAY:
                way.setTag(LOOP_PLAY);
                Log.d("MAYDAY", "onChang被调用啦-----------------------loop");
                way.setImageResource(R.mipmap.loop);
                break;
            case RANDOM_PLAY:
                way.setTag(RANDOM_PLAY);
                Log.d("MAYDAY", "onChang被调用啦-----------------------random");
                way.setImageResource(R.mipmap.random);
                break;
            case ONES_PLAY:
                way.setTag(ONES_PLAY);
                Log.d("MAYDAY", "onChang被调用啦-----------------------ones");
                way.setImageResource(R.mipmap.ones);
                break;
            default:
                break;
        }
        //初始化收藏状态(再次进入这个Activity的时候去判断该歌曲是否已被收藏，若收藏了就为红心)
        initCollect();
    }

    private void initCollect() {
        try {
            Mp3Info likeMp3info=app.dbUtils.findFirst(Selector.from(Mp3Info.class).where(
                    "mp3InfoId", "=",mp3Info.getId()));
            if(likeMp3info!=null){
                isLike.setImageResource(R.mipmap.like);
            }else {
                isLike.setImageResource(R.mipmap.hate);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        unBindPlayService();
        super.onDestroy();
    }

    /**
     * 该方法调试后仍然存在问题
     *
     * @param view
     */
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
            case R.id.way: {
                int mode = (int) way.getTag();
                switch (mode) {
                    case LOOP_PLAY:
                        way.setImageResource(R.mipmap.random);
                        playServer.setPlay_mode(RANDOM_PLAY);
                        way.setTag(RANDOM_PLAY);
                        break;
                    case RANDOM_PLAY:
                        playServer.setPlay_mode(ONES_PLAY);
                        way.setTag(ONES_PLAY);
                        way.setImageResource(R.mipmap.ones);
                        break;
                    case ONES_PLAY:
                        way.setTag(LOOP_PLAY);
                        way.setImageResource(R.mipmap.loop);
                        playServer.setPlay_mode(LOOP_PLAY);
                        break;
                }
            }
            case R.id.isLike: {
                Mp3Info mp3 = mp3Infos.get(playServer.getCurrentPositions());
                try {
                    //得到首个实体对象(这里需要卸载程序后才能看到效果.不然会提示找不到该字段(列))
                    //相当于select * from 表名 where mp3InfoId =  *; 第一次传入的时候为Null，
                    Mp3Info likeMp3info=app.dbUtils.findFirst(Selector.from(Mp3Info.class).where(
                            "mp3InfoId", "=", mp3.getId()));
                    if (likeMp3info == null) {
                        mp3.setMp3InfoId(mp3.getId());
                        app.dbUtils.save(mp3);
                        isLike.setImageResource(R.mipmap.like);
                    } else {
                        app.dbUtils.deleteById(Mp3Info.class, likeMp3info.getId());
                        isLike.setImageResource(R.mipmap.hate);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            default:
                break;
        }

    }
}