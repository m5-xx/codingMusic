package com.mayday.xy.codingmusic.MainActivitys;

import android.os.Handler;

import com.mayday.xy.codingmusic.Utils.Mp3Net;

/**
 * Created by xy-pc on 2016/11/23.
 */

public class DownloadUtils {
    private static DownloadUtils sInstance;
    private static DownMusic downMusic;
//    private Mp3Net mp3;

    public DownloadUtils setListener(DownMusic downMusic){
        this.downMusic=downMusic;
        return this;
    }


    public synchronized static DownloadUtils getInstance(){
        sInstance=new DownloadUtils();
        return sInstance;
    }

    //下载
    public void getDownloadMp3Url(final Mp3Net mp3, Handler handler){



    }

    interface DownMusic{
        //下载歌曲
        void onDownload(String mp3Url);
        //下载出错
        void onError(String error);
    }
}
