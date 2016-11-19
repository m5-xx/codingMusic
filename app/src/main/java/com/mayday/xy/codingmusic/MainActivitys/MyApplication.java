package com.mayday.xy.codingmusic.MainActivitys;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.util.LogUtils;

/**
 * Created by xy-pc on 2016/11/13.
 */

public class MyApplication extends Application {
    public static Context context;

    public static SharedPreferences sp;

    public static DbUtils dbUtils;
    @Override
    public void onCreate() {
        context=getApplicationContext();

        sp=getSharedPreferences("DATA",MODE_PRIVATE);

        dbUtils=DbUtils.create(getApplicationContext(),"COLLECT.db");
        super.onCreate();
    }

    //得到全局的Context
    public static Context getContext(){
        return context;
    }
}
