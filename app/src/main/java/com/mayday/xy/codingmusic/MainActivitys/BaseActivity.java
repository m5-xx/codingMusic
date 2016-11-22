package com.mayday.xy.codingmusic.MainActivitys;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Created by xy-pc on 2016/11/4.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected PlayServer playServer;

    //判断服务是否绑定
    private Boolean isBind=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //连接Server的函数
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
                //支持绑定Server
                PlayServer.PlayBind playBinder=(PlayServer.PlayBind)service;
                //得到PlayServer实例的引用
                playServer=playBinder.getPlayService();
                isBind=true;
            //异步处理
            musicUpdateListener.onChanges(playServer.getCurrentPositions());
            playServer.setMusicUpdateListener(musicUpdateListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playServer=null;
            isBind=false;
        }
    };

    private PlayServer.MusicUpdateListener musicUpdateListener=new PlayServer.MusicUpdateListener() {
        @Override
        public void onProgress(int progress) {
            Progress(progress);
        }

        @Override
        public void onChanges(int position) {
            Changes(position);
        }
    };

    //因为更新的时候涉及到具体的某一个对象，因此需要提供一个抽象的接口来对其子类作用
    public abstract void Progress(int progress);
    public abstract void Changes(int position);


    public void bindPlayService(){
        if(!isBind){
            Intent intent=new Intent(this,PlayServer.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isBind=true;

        }
    }
    public void unBindPlayService(){
        if(isBind){
            unbindService(conn);
            isBind=false;
        }
    }

    /**
     * 当用户退出应用后进行保存
     */
    @Override
    protected void onDestroy() {
        MyApplication app= (MyApplication)getApplication();
//        MyApplication app= (MyApplication) getSharedPreferences("DATA",MODE_PRIVATE);
        SharedPreferences.Editor editor=app.sp.edit();
        editor.putInt("currentPosition",playServer.getCurrentPositions());
        editor.putInt("play_mode",playServer.getPlay_mode());
        editor.commit();
        super.onDestroy();
    }
}
