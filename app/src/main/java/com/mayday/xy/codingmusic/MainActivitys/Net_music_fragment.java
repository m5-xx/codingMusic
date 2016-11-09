package com.mayday.xy.codingmusic.MainActivitys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;
import com.mayday.xy.codingmusic.adapter.My_music_adapter;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/3.
 */
public class Net_music_fragment extends Fragment{
    private static Net_music_fragment music_fragment;

    //提供方法供外界调用
    public static Net_music_fragment newInstance() {
        music_fragment=new Net_music_fragment();
        return music_fragment;
    }


}
